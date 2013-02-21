package com.meetup.attendance.attendance;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.view.View;
import com.meetup.attendance.rest.AttendanceRecord;

public class ChangeListener implements View.OnClickListener {
    private final Context context;
    private final AttendanceRecord record;
    private final String urlname, eventId;

    public ChangeListener(Context context, AttendanceRecord record, String urlname, String eventId) {
        this.context = context;
        this.record = record;
        this.urlname = urlname;
        this.eventId = eventId;
    }

    @Override
    public void onClick(View v) {
        FragmentManager fm = ((Activity)context).getFragmentManager();
        ChangeFragment cf = ChangeFragment.create(record, urlname, eventId);
        cf.show(fm, "change");
    }
}
