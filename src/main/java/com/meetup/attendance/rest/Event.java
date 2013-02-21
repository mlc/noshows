package com.meetup.attendance.rest;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

public class Event implements Parcelable {
    public @JsonProperty("id") String id;
    public @JsonProperty("name") String name;
    public @JsonProperty("group") GroupBasics group;
    public @JsonProperty("time") long time;
    public @JsonProperty("yes_rsvp_count") int yesRsvpCount;
    public @JsonProperty("event_url") String eventUrl;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(name);
        if (group == null) {
            out.writeInt(0);
        } else {
            out.writeInt(1);
            group.writeToParcel(out, flags);
        }
        out.writeLong(time);
        out.writeInt(yesRsvpCount);
        out.writeString(eventUrl);
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            Event e = new Event();
            e.id = in.readString();
            e.name = in.readString();
            e.group = (in.readInt() == 0 ? null : GroupBasics.CREATOR.createFromParcel(in));
            e.time = in.readLong();
            e.yesRsvpCount = in.readInt();
            e.eventUrl = in.readString();
            return e;
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Override
    public String toString() {
        return Objects.toStringHelper(this).
                add("id", id).
                add("name", name).
                add("group", group).
                add("time", time).
                add("yesRsvpCount", yesRsvpCount).
                add("eventUrl", eventUrl).
                toString();
    }
}
