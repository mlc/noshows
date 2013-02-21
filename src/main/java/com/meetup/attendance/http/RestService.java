package com.meetup.attendance.http;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.util.Log;
import android.util.Pair;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.meetup.attendance.RestFragment;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class RestService extends IntentService {
    private static final String TAG = "RestService";
    private static final Splitter.MapSplitter ENTITY_SPLITTER = Splitter.on('&').withKeyValueSeparator(Splitter.on('='));

    public RestService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Uri action = intent.getData();
        Bundle extras = intent.getExtras();
        if (action == null || extras == null || !extras.containsKey("verb") || !extras.containsKey("oauth_mode")) {
            throw new IllegalArgumentException();
        }
        Verb verb = extras.getParcelable("verb");
        OAuthMode oAuthMode = extras.getParcelable("oauth_mode");
        Bundle params = extras.getParcelable("params");
        ResultReceiver receiver = extras.getParcelable("receiver");

        try {
            final HttpUriRequest req;
            switch(verb) {
            case GET:
                req = new HttpGet(convertUri(addQueryParams(action, params)));
                break;
            case DELETE:
                req = new HttpDelete(convertUri(addQueryParams(action, params)));
                break;
            case POST: {
                HttpPost postreq = new HttpPost(convertUri(action));
                extractOauthParams(postreq, params);
                postreq.setEntity(bundleToEntity(params));
                req = postreq;
                break;
            }
            case PUT: {
                HttpPut putreq = new HttpPut(convertUri(action));
                putreq.setEntity(bundleToEntity(params));
                req = putreq;
                break;
            }
            default:
                throw new IllegalStateException();
            }
            final Pair<String, String> token;
            if (extras.containsKey("token") && extras.containsKey("token_secret")) {
                token = Pair.create(extras.getString("token"), extras.getString("token_secret"));
            } else {
                token = null;
            }
            HttpResponse resp = HttpWrapper.getInstance().exec(req, oAuthMode, token);
            HttpEntity entity = resp.getEntity();
            StatusLine statusLine = resp.getStatusLine();
            int statusCode = statusLine == null ? -1 : statusLine.getStatusCode();
            Bundle b;
            if (entity == null) {
                b = Bundle.EMPTY;
            } else {
                b = parseResponse(entity, extras);
            }
            receiver.send(statusCode, b);
        } catch (IOException ex) {
            Log.e(TAG, "IO Exception", ex);
            Bundle b = new Bundle();
            b.putSerializable("exception", ex);
            receiver.send(-1, b);
        }
    }

    private static void extractOauthParams(HttpPost postreq, Bundle params) {
        if (params == null)
            return;

        List<String> toRemove = Lists.newArrayList(Iterables.filter(params.keySet(), new Predicate<String>() {
            @Override
            public boolean apply(@Nullable String input) {
                return (input != null && input.startsWith("oauth_"));
            }
        }));
        StringBuilder bld = new StringBuilder("OAuth ");

        boolean first = true;

        for (String key : toRemove) {
            String val = params.get(key).toString();
            params.remove(key);
            if (!first) {
                bld.append(',');
                first = false;
            }
            bld.append(Uri.encode(key)).append('=').append(Uri.encode(val));
        }
        postreq.setHeader("Authorization", bld.toString());
    }

    private static Bundle parseResponse(HttpEntity entity, Bundle extras) throws IOException {
        try {
            ParseMode mode = extras.getParcelable("parse_mode");
            if (mode == null)
                mode = ParseMode.STRING;
            Bundle b = new Bundle();
            switch (mode) {
            case HTTP_ENTITY:
                putEntity(b, entity);
                break;

            case STRING:
            default:
                b.putString("response", EntityUtils.toString(entity));
                break;
            }
            return b;
        } finally {
            entity.consumeContent();
        }
    }


    private static void putEntity(Bundle b, HttpEntity entity) throws IOException {
        String s = EntityUtils.toString(entity, HTTP.UTF_8);
        for (Map.Entry<String, String> p : ENTITY_SPLITTER.split(s).entrySet()) {
            b.putString(p.getKey(), p.getValue());
        }
    }

    private static HttpEntity bundleToEntity(Bundle params) {
        List<NameValuePair> nvps = Lists.newArrayListWithCapacity(params.size());
        for (String key : params.keySet()) {
            nvps.add(new BasicNameValuePair(key, params.get(key).toString()));
        }
        try {
            return new UrlEncodedFormEntity(nvps, HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    private static Uri addQueryParams(@Nonnull Uri uri, @Nullable Bundle params) {
        if (params == null || params.isEmpty())
            return uri;
        Uri.Builder bld = uri.buildUpon();
        for (String key : params.keySet()) {
            bld.appendQueryParameter(key, params.get(key).toString());
        }
        return bld.build();
    }

    private static URI convertUri(@Nonnull Uri uri) {
        try {
            return URIUtils.createURI(uri.getScheme(), uri.getHost(), uri.getPort(),
                    uri.getEncodedPath(), uri.getEncodedQuery(), uri.getFragment());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static void call(@Nonnull RestFragment caller, @Nonnull Verb verb, @Nonnull Uri uri, @Nullable ParseMode parseMode, @Nullable Bundle params) {
        call(caller, verb, uri, OAuthMode.USER_SIGN, null, parseMode, params);
    }

    public static void call(@Nonnull RestFragment caller, @Nonnull Verb verb, @Nonnull Uri uri, @Nonnull OAuthMode oAuthMode, Pair<String, String> token, @Nullable ParseMode parseMode, @Nullable Bundle params) {
        Context ctx = caller.getActivity();
        Intent intent = new Intent(ctx, RestService.class);
        intent.setData(uri);
        intent.putExtra("verb", (Parcelable)verb);
        intent.putExtra("oauth_mode", (Parcelable)oAuthMode);
        if (parseMode != null)
            intent.putExtra("parse_mode", (Parcelable)parseMode);
        if (token != null) {
            intent.putExtra("token", token.first);
            intent.putExtra("token_secret", token.second);
        }
        if (params != null)
            intent.putExtra("params", params);
        intent.putExtra("receiver", caller.getReceiver());
        ctx.startService(intent);
    }
}