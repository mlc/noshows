package com.meetup.attendance.attendance;

import java.lang.ref.WeakReference;
import android.app.Activity;
import android.os.AsyncTask;
import android.webkit.CookieManager;
import android.webkit.WebViewDatabase;
import com.meetup.attendance.PreferenceUtility;

public class LogOutTask extends AsyncTask<Void, Void, Boolean> {
    private WeakReference<Activity> activityRef;
    private WebViewDatabase webViewDatabase;

    public LogOutTask(Activity activity) {
        this.activityRef = new WeakReference<Activity>(activity);
        webViewDatabase = WebViewDatabase.getInstance(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Activity activity = activityRef.get();
        if (activity != null)
            activity.setProgressBarIndeterminateVisibility(true);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        webViewDatabase.clearFormData();
        webViewDatabase.clearHttpAuthUsernamePassword();
        webViewDatabase.clearFormData();
        CookieManager.getInstance().removeAllCookie();
        return PreferenceUtility.getInstance().setOauthCreds(null, null);
    }

    @Override
    protected void onPostExecute(Boolean success) {
        Activity activity = activityRef.get();
        if (activity != null)
            activity.finish();
    }
}