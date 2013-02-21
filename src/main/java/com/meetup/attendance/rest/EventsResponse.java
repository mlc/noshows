package com.meetup.attendance.rest;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import java.util.List;

public class EventsResponse implements Parcelable {
    public @JsonProperty("results") List<Event> results;
    public @JsonProperty("meta") Bundle meta;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(results);
        dest.writeBundle(meta);
    }
    public static final Creator<EventsResponse> CREATOR = new Creator<EventsResponse>() {
        @Override
        public EventsResponse createFromParcel(Parcel source) {
            EventsResponse er = new EventsResponse();
            er.results = source.createTypedArrayList(Event.CREATOR);
            er.meta = source.readBundle();
            return er;
        }

        @Override
        public EventsResponse[] newArray(int size) {
            return new EventsResponse[size];
        }
    };

    @Override
    public String toString() {
        return Objects.toStringHelper(this).
                add("results", results).
                add("meta", meta).
                toString();
    }
}
