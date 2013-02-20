package com.meetup.attendance;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.meetup.attendance.http.OAuthMode;
import com.meetup.attendance.http.ParseMode;
import com.meetup.attendance.http.RestService;
import com.meetup.attendance.http.Verb;
import org.apache.http.HttpStatus;

public class Auth extends Activity {
    static final String TAG = "Auth";
    static final String CALLBACK_URI = "noshows://oauth/callback";
    private WebView webView;
    private View empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auth);

        webView = (WebView)findViewById(R.id.auth_webview);
        webView.setWebViewClient(new WebClient());
        empty = findViewById(android.R.id.empty);

        FragmentManager fm = getFragmentManager();
        RequestTokenFragment frag = (RequestTokenFragment)fm.findFragmentByTag("request_token");
        if (frag == null) {
            frag = new RequestTokenFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(frag, "request_token");
            ft.commit();
            setEmpty(true);
        } else {
            if (frag.getToken() == null) {
                setEmpty(true);
            } else {
                gotToken();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("is_empty", isEmpty());
    }

    private void setEmpty(boolean isEmpty) {
        webView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        empty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }
    private boolean isEmpty() {
        return empty.getVisibility() != View.GONE;
    }

    void gotToken() {
        setEmpty(false);
        RequestTokenFragment frag = (RequestTokenFragment)getFragmentManager().findFragmentByTag("request_token");
        webView.loadUrl("http://www.meetup.com/authorize/?oauth_token=" + frag.getToken());
    }

    void callback(String url) {
        Uri uri = Uri.parse(url);
        String token = uri.getQueryParameter("oauth_token");
        String verifier = uri.getQueryParameter("oauth_verifier");
    }

    private class WebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(CALLBACK_URI)) {
                callback(url);
                return true;
            } else {
                return false;
            }
        }
    }

    private static class RequestTokenFragment extends RestFragment {
        private static final String TAG = "RequestTokenFragment";
        private static final Uri ENDPOINT = Uri.parse("https://api.meetup.com/oauth/request/");
        private String token, tokenSecret;

        public String getToken() {
            return token;
        }

        public String getTokenSecret() {
            return tokenSecret;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            if (token == null) {
                Bundle args = new Bundle();
                args.putString("oauth_callback", CALLBACK_URI);
                RestService.call(this, Verb.POST, ENDPOINT, OAuthMode.APP_SIGN, ParseMode.HTTP_ENTITY, args);
            }
        }

        @Override
        protected void onRestResult(int resultCode, Bundle data) {
            if (resultCode == HttpStatus.SC_OK) {
                Log.i(TAG, "got request token");
                token = data.getString("oauth_token");
                tokenSecret = data.getString("oauth_token_secret");
                Auth activity = (Auth)getActivity();
                if (activity != null)
                    activity.gotToken();
            } else {
                Log.e(TAG, "unwanted result code " + resultCode + ", data=" + String.valueOf(data));
            }
        }
    }

    private static class AccessTokenFragment extends RestFragment {

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

        }

        @Override
        protected void onRestResult(int resultCode, Bundle data) {
        }
    }
}
