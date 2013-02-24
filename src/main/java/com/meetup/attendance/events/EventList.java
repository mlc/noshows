package com.meetup.attendance.events;

import java.util.List;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import com.meetup.attendance.PreferenceUtility;
import com.meetup.attendance.R;
import com.meetup.attendance.attendance.Attendance;
import com.meetup.attendance.auth.Auth;
import com.meetup.attendance.rest.Event;
import com.meetup.attendance.rest.EventsResponse;

public class EventList extends ListActivity {
    EventAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!PreferenceUtility.getInstance().isLoggedIn()) {
            startActivity(new Intent(this, Auth.class));
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminate(true);
        setProgressBarIndeterminateVisibility(true);

        FragmentManager fm = getFragmentManager();
        EventLoadFragment elf = (EventLoadFragment)fm.findFragmentByTag("event_load");

        adapter = new EventAdapter(this);
        setListAdapter(adapter);

        if (elf == null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(new EventLoadFragment(), "event_load");
            ft.commit();
        } else if (elf.haveData()) {
            loadData(elf.getResponse());
        }
    }

    void loadData(EventsResponse response) {
        setProgressBarIndeterminate(false);
        setProgressBarIndeterminateVisibility(false);

        List<Event> results = response.results;
        adapter.clear();
        adapter.addAll(results);
        long now = System.currentTimeMillis();
        int r = 0;
        for (int i = 0, size = results.size(); i < size; ++i) {
            if (results.get(i).time >= now) {
                r = i;
                break;
            }
        }
        if (r == 0)
            r = results.size() - 1;
        getListView().smoothScrollToPositionFromTop(r, 0, 0);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Event e = adapter.getItem(position);
        Intent i = new Intent(this, Attendance.class)
                .putExtra("title", e.name)
                .putExtra("urlname", e.group.urlname)
                .putExtra("event_id", e.id)
                .putExtra("url", Uri.parse(e.eventUrl));
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.logout:
            logOut();
            return true;

        default:
            return super.onOptionsItemSelected(item);
        }
    }

    void logOut() {
        new LogOutTask(this).execute();
    }

}