package com.meetup.attendance.http;

import android.os.Parcel;
import android.os.Parcelable;

public enum Verb implements Parcelable {
    GET, DELETE, POST, PUT;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.ordinal());
    }

    public static final Creator<Verb> CREATOR = new Creator<Verb>() {
        private final Verb[] vals = Verb.values();
        @Override
        public Verb createFromParcel(Parcel in) {
            return vals[in.readInt()];
        }

        @Override
        public Verb[] newArray(int i) {
            return new Verb[i];
        }
    };

}