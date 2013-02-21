package com.meetup.attendance.http;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.core.type.TypeReference;
import com.meetup.attendance.rest.AttendanceRecord;
import com.meetup.attendance.rest.EventsResponse;

import java.util.ArrayList;

public enum ParseMode implements Parcelable {
    STRING, HTTP_ENTITY,
    EVENTS_RESPONSE(EventsResponse.class),
    ATTENDANCE_RECORDS(new TypeReference<ArrayList<AttendanceRecord>>() {});

    private final Object jsonType;

    private ParseMode() {
        this(null);
    }

    private ParseMode(Object jsonType) {
        this.jsonType = jsonType;
    }

    public Object getJsonType() {
        return jsonType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.ordinal());
    }

    public static final Creator<ParseMode> CREATOR = new Creator<ParseMode>() {
        private final ParseMode[] vals = ParseMode.values();
        @Override
        public ParseMode createFromParcel(Parcel in) {
            return vals[in.readInt()];
        }

        @Override
        public ParseMode[] newArray(int i) {
            return new ParseMode[i];
        }
    };
}
