package com.meetup.attendance.attendance;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.Window;
import com.meetup.attendance.rest.AttendanceRecord;

import java.util.List;

public class Attendance extends ListActivity {

    private AttendanceAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminate(true);
        setProgressBarIndeterminateVisibility(true);

        FragmentManager fm = getFragmentManager();
        AttendanceLoadFragment alf = (AttendanceLoadFragment)fm.findFragmentByTag("attendance_load");
        if (alf == null) {
            alf = new AttendanceLoadFragment();
            alf.setArguments(getIntent().getExtras());
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(alf, "attendance_load");
            ft.commit();
        }
        if (alf.getRecords() != null)
            loaded(alf.getRecords());

        adapter = new AttendanceAdapter(this);
        setListAdapter(adapter);
    }

    void loaded(List<AttendanceRecord> records) {
        adapter.clear();
        adapter.addAll(records);
    }
}