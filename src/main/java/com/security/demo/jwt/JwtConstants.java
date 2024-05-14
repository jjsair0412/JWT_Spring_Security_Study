package com.security.demo.jwt;

public class JwtConstants {
    // Expiration Time
    public static final long MINUTE = 1000 * 60;
    public static final long HOUR = 60 * MINUTE;
    public static final long DAY = 24 * HOUR;
    public static final long WEEK = 7 * DAY;
    public static final long MONTH = 30 * DAY;

    public static final long AT_EXP_TIME =  1 * MINUTE;
    public static final long RT_EXP_TIME =  10 * MINUTE;
}
