package com.meetup.attendance;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PreferenceUtility {
    private static PreferenceUtility instance;
    private SharedPreferences prefs;

    private PreferenceUtility(Context ctx) {
        prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static synchronized PreferenceUtility getInstance() {
        if (instance == null)
            instance = new PreferenceUtility(NoshowsApplication.getInstance());
        return instance;
    }

    public @Nonnull Pair<String, String> getOauthCreds() {
        return Pair.create(prefs.getString("oauth_token", null),
                prefs.getString("oauth_secret", null));
    }

    public boolean isLoggedIn() {
        return prefs.contains("oauth_token") && prefs.contains("oauth_secret");
    }

    public void setOauthCreds(@Nullable String token, @Nullable String secret) {
        SharedPreferences.Editor ed = prefs.edit();
        if (token == null || secret == null) {
            ed.remove("oauth_token");
            ed.remove("oauth_secret");
        } else {
            ed.putString("oauth_token", token);
            ed.putString("oauth_secret", secret);
        }
        ed.apply();
    }
}