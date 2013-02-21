package com.meetup.attendance.auth;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import com.meetup.attendance.http.OAuthMode;
import com.meetup.attendance.http.ParseMode;
import com.meetup.attendance.http.RestService;
import com.meetup.attendance.http.Verb;
import com.meetup.attendance.rest.RestFragment;
import org.apache.http.HttpStatus;

import static com.google.common.base.Preconditions.checkArgument;

class RequestTokenFragment extends RestFragment {
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
            args.putString("oauth_callback", Auth.CALLBACK_URI);
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
