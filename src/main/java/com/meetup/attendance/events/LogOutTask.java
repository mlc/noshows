package com.meetup.attendance.events;

import java.lang.ref.WeakReference;
import android.app.Activity;
import android.os.AsyncTask;
import android.webkit.CookieManager;
import android.webkit.WebViewDatabase;
import com.meetup.attendance.PreferenceUtility;

class LogOutTask extends AsyncTask<Void, Void, Boolean> {
    private WeakReference<Activity> activityRef;
    private WebViewDatabase webViewDatabase;

    public LogOutTask(Activity activity) {
        this.activityRef = new WeakReference<Activity>(activity);
        webViewDatabase = WebViewDatabase.getInstance(activity);
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
    protected void onPostExecute(Boolean dummy) {
        Activity activity = activityRef.get();
        if (activity != null)
            activity.finish();
    }
}
