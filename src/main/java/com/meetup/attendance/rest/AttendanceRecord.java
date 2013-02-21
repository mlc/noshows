package com.meetup.attendance.rest;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AttendanceRecord implements Parcelable {
    public @JsonProperty("member") MemberBasics member;
    public @JsonProperty("rsvp") Rsvp rsvp;
    public @JsonProperty("status") Status status;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        maybeWrite(dest, flags, member);
        maybeWrite(dest, flags, rsvp);
        maybeWrite(dest, flags, status);
    }

    public static final Creator<AttendanceRecord> CREATOR = new Creator<AttendanceRecord>() {
        @Override
        public AttendanceRecord createFromParcel(Parcel in) {
            AttendanceRecord ar = new AttendanceRecord();
            ar.member = maybeRead(in, MemberBasics.CREATOR);
            ar.rsvp = maybeRead(in, Rsvp.CREATOR);
            ar.status = maybeRead(in, Status.CREATOR);
            return ar;
        }

        @Override
        public AttendanceRecord[] newArray(int size) {
            return new AttendanceRecord[size];
        }
    };

    static void maybeWrite(Parcel out, int flags, Parcelable obj) {
        if (obj == null) {
            out.writeInt(0);
        } else {
            out.writeInt(1);
            obj.writeToParcel(out, flags);
        }
    }

    static <T extends Parcelable> T maybeRead(Parcel in, Creator<T> creator) {
        if (in.readInt() == 0)
            return null;
        else
            return creator.createFromParcel(in);
    }

    public static class Rsvp implements Parcelable {
        public @JsonProperty("guests") int guests;
        public @JsonProperty("response") Response response;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(guests);
            maybeWrite(out, flags, response);
        }

        public static final Creator<Rsvp> CREATOR = new Creator<Rsvp>() {
            @Override
            public Rsvp createFromParcel(Parcel in) {
                Rsvp r = new Rsvp();
                r.guests = in.readInt();
                r.response = maybeRead(in, Response.CREATOR);
                return r;
            }

            @Override
            public Rsvp[] newArray(int size) {
                return new Rsvp[size];
            }
        };
    }

    public static enum Response implements Parcelable {
        maybe, waitlist, yes, no, havent;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.ordinal());
        }

        public static final Creator<Response> CREATOR = new Creator<Response>() {
            private final Response[] vals = Response.values();

            @Override
            public Response createFromParcel(Parcel source) {
                return vals[source.readInt()];
            }

            @Override
            public Response[] newArray(int size) {
                return new Response[size];
            }
        };

    }

    public static enum Status implements Parcelable {
        noshow, absent, attended;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.ordinal());
        }

        public static final Creator<Status> CREATOR = new Creator<Status>() {
            private final Status[] vals = Status.values();

            @Override
            public Status createFromParcel(Parcel source) {
                return vals[source.readInt()];
            }

            @Override
            public Status[] newArray(int size) {
                return new Status[size];
            }
        };
    }
}
