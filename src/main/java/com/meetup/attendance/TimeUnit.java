package com.meetup.attendance;

public class TimeUnit {
    private TimeUnit() { }

    public static final long MS = 1L,
            SECOND = 1000*MS,
            MINUTE = 60*SECOND,
            HOUR = 60*MINUTE,
            DAY = 24*HOUR,
            WEEK = 7*DAY;
}
