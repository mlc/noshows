package com.meetup.attendance.http;

import android.os.Parcel;
import android.os.Parcelable;

public enum OAuthMode implements Parcelable {
    DONT_SIGN, APP_SIGN, USER_SIGN;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.ordinal());
    }

    public static final Creator<OAuthMode> CREATOR = new Creator<OAuthMode>() {
        private final OAuthMode[] vals = OAuthMode.values();
        @Override
        public OAuthMode createFromParcel(Parcel in) {
            return vals[in.readInt()];
        }

        @Override
        public OAuthMode[] newArray(int i) {
            return new OAuthMode[i];
        }
    };
}
