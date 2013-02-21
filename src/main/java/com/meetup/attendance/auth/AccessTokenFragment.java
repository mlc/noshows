package com.meetup.attendance.auth;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import com.meetup.attendance.PreferenceUtility;
import com.meetup.attendance.http.OAuthMode;
import com.meetup.attendance.http.ParseMode;
import com.meetup.attendance.http.RestService;
import com.meetup.attendance.http.Verb;
import com.meetup.attendance.rest.RestFragment;

import static com.google.common.base.Preconditions.checkArgument;

class AccessTokenFragment extends RestFragment {
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
