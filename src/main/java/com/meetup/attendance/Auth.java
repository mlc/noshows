package com.meetup.attendance;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.google.common.base.Objects;
import com.meetup.attendance.http.OAuthMode;
import com.meetup.attendance.http.ParseMode;
import com.meetup.attendance.http.RestService;
import com.meetup.attendance.http.Verb;
import org.apache.http.HttpStatus;

import static com.google.common.base.Preconditions.checkArgument;

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
        RequestTokenFragment rtf = (RequestTokenFragment)fm.findFragmentByTag("request_token");
        AccessTokenFragment atf = (AccessTokenFragment)fm.findFragmentByTag("access_token");
        if (atf != null) {
            setEmpty(true);
            if (atf.isDone())
                loggedIn();
        } else if (rtf != null) {
            if (rtf.getToken() == null) {
                setEmpty(true);
            } else {
                gotToken();
            }
        } else {
            rtf = new RequestTokenFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(rtf, "request_token");
            ft.commit();
            setEmpty(true);
        }
        if (savedInstanceState != null && savedInstanceState.containsKey("is_empty"))
            setEmpty(savedInstanceState.getBoolean("is_empty"));
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

    void loggedIn() {
        Intent returnTo = getIntent().getParcelableExtra("return_to");
        if (returnTo == null)
            returnTo = new Intent(this, Attendance.class);
        startActivity(returnTo);
        finish();
    }

    void callback(String url) {
        Uri uri = Uri.parse(url);
        String token = uri.getQueryParameter("oauth_token");
        String verifier = uri.getQueryParameter("oauth_verifier");
        setEmpty(true);

        FragmentManager fm = getFragmentManager();
        RequestTokenFragment rtf = (RequestTokenFragment)fm.findFragmentByTag("request_token");
        if (rtf == null)
            throw new IllegalStateException();
        if (!Objects.equal(token, rtf.getToken()))
            throw new IllegalStateException();
        AccessTokenFragment atf = AccessTokenFragment.create(token, rtf.getTokenSecret(), verifier);
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(atf, "access_token");
        ft.commit();
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
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            checkArgument(activity instanceof Auth, "activity is %s, expected Auth", activity.getClass());
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            if (token == null) {
                Bundle args = new Bundle();
                args.putString("oauth_callback", CALLBACK_URI);
                RestService.call(this, Verb.POST, ENDPOINT, OAuthMode.APP_SIGN, null, ParseMode.HTTP_ENTITY, args);
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
        private static final String TAG = "AccessTokenFragment";
        private static final Uri ENDPOINT = Uri.parse("https://api.meetup.com/oauth/access/");
        private boolean done = false;

        public static AccessTokenFragment create(String token, String tokenSecret, String verifier) {
            Bundle args = new Bundle();
            args.putString("token", token);
            args.putString("token_secret", tokenSecret);
            args.putString("verifier", verifier);
            AccessTokenFragment atf = new AccessTokenFragment();
            atf.setArguments(args);
            return atf;
        }

        public String getToken() {
            return getArguments().getString("token");
        }

        public String getTokenSecret() {
            return getArguments().getString("token_secret");
        }

        public String getVerifier() {
            return getArguments().getString("verifier");
        }

        public boolean isDone() {
            return done;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            checkArgument(activity instanceof Auth, "activity is %s, expected Auth", activity.getClass());
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            Bundle args = new Bundle();
            args.putString("oauth_verifier", getVerifier());
            Pair<String, String> token = Pair.create(getToken(), getTokenSecret());
            RestService.call(this, Verb.POST, ENDPOINT, OAuthMode.CUSTOM_SIGN, token, ParseMode.HTTP_ENTITY, args);
        }

        @Override
        protected void onRestResult(int resultCode, Bundle data) {
            String token = data.getString("oauth_token");
            String tokenSecret = data.getString("oauth_token_secret");
            PreferenceUtility.getInstance().setOauthCreds(token, tokenSecret);
            Auth activity = (Auth)getActivity();
            if (activity != null)
                activity.loggedIn();
            done = true;
        }
    }
}
