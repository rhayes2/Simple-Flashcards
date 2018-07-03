package com.randomappsinc.simpleflashcards.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeUtils {

    private static final int SECONDS_PER_MINUTE = 60;
    private static final int SECONDS_PER_HOUR = 60 * SECONDS_PER_MINUTE;

    private static final String DATE_FORMAT = "MM/dd/yyyy";

    public static String getFlashcardSetTime(long unixTime) {
        Date date = new Date(unixTime);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        return simpleDateFormat.format(date);
    }

    // Given a number of seconds, returns it in a hh:mm:ss format
    public static String getSecondsAsCountdown(int numSeconds) {
        int remainingHours = numSeconds / SECONDS_PER_HOUR;
        int withHoursRemoved = numSeconds % SECONDS_PER_HOUR;
        int remainingMinutes = withHoursRemoved / SECONDS_PER_MINUTE;
        int remainingSeconds = withHoursRemoved % SECONDS_PER_MINUTE;
        return String.format(Locale.getDefault(), "%02d", remainingHours)
                + ":" + String.format(Locale.getDefault(), "%02d", remainingMinutes)
                + ":" + String.format(Locale.getDefault(), "%02d", remainingSeconds);
    }
}
