package com.meetup.attendance.attendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.meetup.attendance.R;
import com.meetup.attendance.rest.AttendanceRecord;

public class AttendanceAdapter extends ArrayAdapter<AttendanceRecord> {
    private final LayoutInflater inflater;

    public AttendanceAdapter(Context context) {
        super(context, 0);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public boolean hasStableIds() {
        return super.hasStableIds();
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).member.id;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final Tag tag;
        if (view == null) {
            view = inflater.inflate(R.layout.list_item_attendance, parent, false);
            tag = new Tag(view);
            view.setTag(tag);
        } else {
            tag = (Tag)view.getTag();
        }
        AttendanceRecord ar = getItem(position);
        tag.memberName.setText(ar.member.name);
        tag.memberStatus.setText(ar.rsvp.response.toString() + ", " + ar.status.toString());
        return view;
    }

    private static class Tag {
        public final TextView memberName;
        public final TextView memberStatus;
        public final Button change;

        private Tag(View v) {
            memberName = (TextView)v.findViewById(R.id.member_name);
            memberStatus = (TextView)v.findViewById(R.id.member_status);
            change = (Button)v.findViewById(R.id.change);
        }
    }
}
