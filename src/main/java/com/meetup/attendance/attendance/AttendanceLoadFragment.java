package com.meetup.attendance.attendance;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import com.meetup.attendance.http.ParseMode;
import com.meetup.attendance.http.RestService;
import com.meetup.attendance.http.Verb;
import com.meetup.attendance.rest.AttendanceRecord;
import com.meetup.attendance.rest.RestFragment;

import java.util.ArrayList;

import static com.google.common.base.Preconditions.checkState;

class AttendanceLoadFragment extends RestFragment {
    private ArrayList<AttendanceRecord> records;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        checkState(activity instanceof Attendance);
    }

    public String getUrlname() {
        return getArguments().getString("urlname");
    }

    public String getEventId() {
        return getArguments().getString("event_id");
    }

    public ArrayList<AttendanceRecord> getRecords() {
        return records;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Uri uri = getUri(getUrlname(), getEventId());
        Bundle args = new Bundle();
        args.putString("filter", "yes");
        RestService.call(this, Verb.GET, uri, ParseMode.ATTENDANCE_RECORDS, args);
    }

    public static Uri getUri(String urlname, String eventId) {
        return new Uri.Builder()
                    .scheme("https")
                    .authority("api.meetup.com")
                    .appendPath(urlname)
                    .appendPath("events")
                    .appendPath(eventId)
                    .appendPath("attendance")
                    .build();
    }

    @Override
    protected void onRestResult(int resultCode, Bundle data) {
        records = data.getParcelableArrayList("json");
        Attendance activity = (Attendance)getActivity();
        if (activity != null) {
            if (records == null)
                activity.finish();
            else
                activity.loaded(records);
        }
    }
}
