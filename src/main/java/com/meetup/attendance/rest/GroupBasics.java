package com.meetup.attendance.rest;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

public class GroupBasics implements Parcelable {
    public @JsonProperty("group_lat") double lat;
    public @JsonProperty("group_lon") double lon;
    public @JsonProperty("id") long id;
    public @JsonProperty("name") String name;
    public @JsonProperty("urlname") String urlname;
    public @JsonProperty("who") String who;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(lat);
        dest.writeDouble(lon);
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(urlname);
        dest.writeString(who);
    }
    public static final Creator<GroupBasics> CREATOR = new Creator<GroupBasics>() {
        @Override
        public GroupBasics createFromParcel(Parcel in) {
            GroupBasics gb = new GroupBasics();
            gb.lat = in.readDouble();
            gb.lon = in.readDouble();
            gb.id = in.readLong();
            gb.name = in.readString();
            gb.urlname = in.readString();
            gb.who = in.readString();
            return gb;
        }

        @Override
        public GroupBasics[] newArray(int size) {
            return new GroupBasics[size];
        }
    };

    @Override
    public String toString() {
        return Objects.toStringHelper(this).
                add("lat", lat).
                add("lon", lon).
                add("id", id).
                add("name", name).
                add("urlname", urlname).
                add("who", who).
                toString();
    }
}
