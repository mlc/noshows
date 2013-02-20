package com.meetup.attendance;

import android.app.Application;
import android.content.pm.PackageInfo;
//import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

import static com.google.common.base.Preconditions.checkState;

@ReportsCrashes(
        formKey = "",
        formUri = "https://noshows.iriscouch.com/acra-noshows/_design/acra-storage/_update/report",
        reportType = HttpSender.Type.JSON,
        httpMethod = HttpSender.Method.PUT,
        formUriBasicAuthLogin = "reporter",
        formUriBasicAuthPassword = "ub5Quah6",
        mode = ReportingInteractionMode.SILENT
)
public class NoshowsApplication extends Application {
    private static NoshowsApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        //ACRA.init(this);
        instance = this;
    }

    public static NoshowsApplication getInstance() {
        checkState(instance != null, "called NoshowsApplication.getInstance() before onCreate() finished");
        return instance;
    }

    public String getVersion() {
        try {
            PackageInfo pi = getPackageManager().getPackageInfo("com.meetup.attendance", 0);
            return pi.versionName;
        } catch (Exception whatever) {
            return "";
        }
    }
}
