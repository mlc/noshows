package com.meetup.attendance.attendance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.google.common.base.Optional;
import com.meetup.attendance.R;
import com.meetup.attendance.rest.AttendanceRecord;

public class AttendanceAdapter extends ArrayAdapter<AttendanceRecord> {
    private final LayoutInflater inflater;
    private final String urlname, eventId;

    public AttendanceAdapter(Attendance context) {
        super(context, 0);
        inflater = LayoutInflater.from(context);
        urlname = context.getIntent().getStringExtra("urlname");
        eventId = context.getIntent().getStringExtra("event_id");
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).member.id;
    }

    public Optional<AttendanceRecord> getItemByMemberId(long id) {
        for (int i = 0, size = getCount(); i < size; ++i) {
            AttendanceRecord ar = getItem(i);
            if (ar.member.id == id)
                return Optional.of(ar);
        }
        return Optional.absent();
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
        tag.change.setOnClickListener(new ChangeListener(getContext(), ar, urlname, eventId));
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
