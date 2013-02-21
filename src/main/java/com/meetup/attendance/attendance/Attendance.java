package com.meetup.attendance.attendance;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import com.meetup.attendance.R;
import com.meetup.attendance.rest.AttendanceRecord;

import java.util.List;

public class Attendance extends ListActivity {

    private AttendanceAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminate(true);
        setProgressBarIndeterminateVisibility(true);

        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getIntent().getStringExtra("title"));

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_attendance, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
        case R.id.view_event:
            viewEvent();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    void viewEvent() {
        Intent i = new Intent(Intent.ACTION_VIEW, getIntent().<Uri>getParcelableExtra("url"))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}