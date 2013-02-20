package com.meetup.attendance.http;

import android.os.Parcel;
import android.os.Parcelable;

public enum ParseMode implements Parcelable {
    STRING, HTTP_ENTITY;

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
