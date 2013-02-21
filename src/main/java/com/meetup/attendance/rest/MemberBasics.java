package com.meetup.attendance.rest;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MemberBasics implements Parcelable {
    public @JsonProperty("id") long id;
    public @JsonProperty("name") String name;
    public @JsonProperty("photo") Bundle photo;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(name);
        out.writeBundle(photo);
    }

    public static final Creator<MemberBasics> CREATOR = new Creator<MemberBasics>() {
        @Override
        public MemberBasics createFromParcel(Parcel in) {
            MemberBasics mb = new MemberBasics();
            mb.id = in.readLong();
            mb.name = in.readString();
            mb.photo = in.readBundle();
            return mb;
        }

        @Override
        public MemberBasics[] newArray(int size) {
            return new MemberBasics[size];
        }
    };
}
