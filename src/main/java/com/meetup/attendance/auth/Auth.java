package com.meetup.attendance.auth;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.google.common.base.Objects;
import com.meetup.attendance.R;
import com.meetup.attendance.events.EventList;

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
        WebSettings webSettings = webView.getSettings();
        webSettings.setSavePassword(false);

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
        startActivity(new Intent(this, EventList.class));
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

}
