package com.meetup.attendance;

import android.app.Application;
import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

@ReportsCrashes(
        formKey = "",
        formUri = "http://https://noshows.iriscouch.com/acra-noshows/_design/acra-storage/_update/report",
        reportType = HttpSender.Type.JSON,
        httpMethod = HttpSender.Method.PUT,
        formUriBasicAuthLogin = "reporter",
        formUriBasicAuthPassword = "ub5Quah6",
        mode = ReportingInteractionMode.SILENT
)
public class NoshowsApplication extends Application {

    @Override
    public void onCreate() {
        ACRA.init(this);
        super.onCreate();
    }

}
