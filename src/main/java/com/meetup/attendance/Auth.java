package com.meetup.attendance;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import com.meetup.attendance.http.OAuthMode;
import com.meetup.attendance.http.ParseMode;
import com.meetup.attendance.http.RestService;
import com.meetup.attendance.http.Verb;

public class Auth extends Activity {
    static final Uri CALLBACK_URI = Uri.parse("noshows://oauth/callback");
    private WebView webView;
    private View empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auth);

        webView = (WebView)findViewById(R.id.auth_webview);
        empty = findViewById(android.R.id.empty);

        FragmentManager fm = getFragmentManager();
        RequestTokenFragment frag = (RequestTokenFragment)fm.findFragmentByTag("request_token");
        if (frag == null) {
            frag = new RequestTokenFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(frag, "request_token");
            ft.commit();
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

    private static class RequestTokenFragment extends RestFragment {
        private static final String TAG = "RequestTokenFragment";
        private static final Uri ENDPOINT = Uri.parse("https://api.meetup.com/oauth/request/");

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            Bundle args = new Bundle();
            args.putParcelable("oauth_callback", CALLBACK_URI);
            RestService.call(this, Verb.POST, ENDPOINT, OAuthMode.APP_SIGN, ParseMode.HTTP_ENTITY, args);
        }

        @Override
        protected void onRestResult(int resultCode, Bundle data) {
            Log.d(TAG, "onRestResult " + resultCode);
            Log.d(TAG, String.valueOf(data));
        }
    }
}
