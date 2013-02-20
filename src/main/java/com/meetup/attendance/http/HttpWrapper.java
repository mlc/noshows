package com.meetup.attendance.http;

import android.os.Build;
import android.util.Pair;
import com.google.common.base.Ascii;
import com.google.common.base.Objects;
import com.google.common.net.HttpHeaders;
import com.meetup.attendance.NoshowsApplication;
import com.meetup.attendance.PreferenceUtility;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthException;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Locale;

import static com.google.common.base.Preconditions.checkState;

public class HttpWrapper {
    private static final HttpWrapper INSTANCE = new HttpWrapper();

    private final HttpClient client;
    private final CommonsHttpOAuthConsumer oAuthConsumer;

    private HttpWrapper() {
        HttpParams params = new BasicHttpParams();
        HttpClientParams.setRedirecting(params, false);
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
        HttpProtocolParams.setHttpElementCharset(params, HTTP.UTF_8);
        String ua = "Meetup-Noshows/" + NoshowsApplication.getInstance().getVersion() + " Android/" + Build.VERSION.RELEASE;
        HttpProtocolParams.setUserAgent(params, ua);

        SchemeRegistry sr = new SchemeRegistry();
        sr.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        sr.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, sr);
        DefaultHttpClient cli = new DefaultHttpClient(ccm, params);
        cli.addRequestInterceptor(new HttpRequestInterceptor() {
            @Override
            public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
                request.addHeader(HttpHeaders.ACCEPT_CHARSET, "utf-8");
                request.addHeader(HttpHeaders.ACCEPT, "application/json");
                request.addHeader(HttpHeaders.ACCEPT_LANGUAGE, Ascii.toLowerCase(Locale.getDefault().getLanguage())); // may vary during app lifespan, fwiw
                request.addHeader(HttpHeaders.ACCEPT_ENCODING, "gzip");
            }
        });
        cli.addResponseInterceptor(new HttpResponseInterceptor() {
            @Override
            public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
                final HttpEntity entity = response.getEntity();
                final Header encoding = entity.getContentEncoding();
                if (encoding != null) {
                    for (HeaderElement elt : encoding.getElements()) {
                        if (Objects.equal(elt.getName(), "gzip")) {
                            response.setEntity(new GzipEntity(response.getEntity()));
                            break;
                        }
                    }
                }
            }
        });

        oAuthConsumer = new CommonsHttpOAuthConsumer(Secrets.CONSUMER_KEY, Secrets.CONSUMER_SECRET);
        oAuthConsumer.setSendEmptyTokens(false);

        client = cli;
    }

    public static HttpWrapper getInstance() {
        return INSTANCE;
    }

    public HttpResponse exec(HttpUriRequest request, OAuthMode authMode) throws IOException {
        if (authMode != OAuthMode.DONT_SIGN) {
            final @Nonnull Pair<String, String> token;
            if (authMode == OAuthMode.APP_SIGN) {
                token = Pair.create(null, null);
            } else {
                token = PreferenceUtility.getInstance().getOauthCreds();
                checkState(token.first != null && token.second != null, "must be logged in to do a fully-signed OAuth request");
            }
            try {
                synchronized (oAuthConsumer) {
                    oAuthConsumer.setTokenWithSecret(token.first, token.second);
                    oAuthConsumer.sign(request);
                }
            } catch (OAuthException ex) {
                throw new RuntimeException(ex);
            }
        }
        return client.execute(request);
    }
}