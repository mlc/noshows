package com.meetup.attendance.events;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import com.meetup.attendance.TimeUnit;
import com.meetup.attendance.http.ParseMode;
import com.meetup.attendance.http.RestService;
import com.meetup.attendance.http.Verb;
import com.meetup.attendance.rest.EventsResponse;
import com.meetup.attendance.rest.RestFragment;

public class EventLoadFragment extends RestFragment {
    private static final String TAG = "EventLoadFragment";
    private static final Uri ENDPOINT = Uri.parse("https://api.meetup.com/2/events");

    private EventsResponse response;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof EventList))
            throw new IllegalArgumentException();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle args = new Bundle();
        args.putString("member_id", "self");
        args.putString("status", "past,upcoming");
        long now = System.currentTimeMillis();
        args.putString("time", (now - 1*TimeUnit.WEEK) + "," + now);
        args.putInt("page", 100);
        args.putString("only", "id,name,group,time,yes_rsvp_count,event_url");
        RestService.call(this, Verb.GET, ENDPOINT, ParseMode.EVENTS_RESPONSE, args);
    }

    @Override
    protected void onRestResult(int resultCode, Bundle data) {
        //Log.d(TAG, Objects.toStringHelper("onRestResult").add("resultCode", resultCode).add("data", data).toString());
        response = data.getParcelable("json");
        EventList activity = (EventList)getActivity();
        if (activity != null)
            activity.loadData(response);
    }

    public boolean haveData() {
        return response != null;
    }

    public EventsResponse getResponse() {
        return response;
    }
}
