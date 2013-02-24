package com.meetup.attendance.attendance;

import java.util.List;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import com.google.common.base.Optional;
import com.meetup.attendance.R;
import com.meetup.attendance.rest.AttendanceRecord;

public class Attendance extends ListActivity implements ChangeFragment.Contract {

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
        adapter.setNotifyOnChange(false);
        setListAdapter(adapter);
    }

    void loaded(List<AttendanceRecord> records) {
        adapter.clear();
        adapter.addAll(records);
        adapter.notifyDataSetChanged();
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

    @Override
    public void notifyChange(long memberId, AttendanceRecord.Status newStatus) {
        Optional<AttendanceRecord> oar = adapter.getItemByMemberId(memberId);
        if (oar.isPresent()) {
            oar.get().status = newStatus;
            adapter.notifyDataSetChanged();
        }
    }
}