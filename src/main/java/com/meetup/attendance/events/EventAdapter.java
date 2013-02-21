package com.meetup.attendance.events;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.google.common.base.CharMatcher;
import com.meetup.attendance.rest.Event;

public class EventAdapter extends ArrayAdapter<Event> {
    private final LayoutInflater inflater;
    private static final CharMatcher DIGITS = CharMatcher.inRange('0', '9');

    public EventAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_2);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null)
            view = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
        TextView t1 = (TextView)view.findViewById(android.R.id.text1);
        TextView t2 = (TextView)view.findViewById(android.R.id.text2);
        Event event = getItem(position);
        t1.setText(event.name);
        t2.setText(DateUtils.formatDateTime(getContext(), event.time, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME));
        return view;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int position) {
        String id = getItem(position).id;
        if (DIGITS.matchesAllOf(id))
            return Long.parseLong(id, 10);
        else
            return Long.parseLong(id, 36);
    }
}
