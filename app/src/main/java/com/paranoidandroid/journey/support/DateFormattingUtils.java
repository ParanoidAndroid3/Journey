package com.paranoidandroid.journey.support;

import android.content.Context;
import android.text.format.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public final class DateFormattingUtils {

    public static String formatDateRange(Context context, Date start, Date end) {
        if (start != null && end != null) {
            return DateUtils.formatDateRange(context, start.getTime(), end.getTime(), DateUtils.FORMAT_ABBREV_ALL);
        }
        return "";
    }

    /**
     * Get the number of days between start and end as a string.
     */
    public static String formatDurationInDays(Date start, Date end) {
        if (start != null &&  end != null) {
            // TODO(emmanuel): maybe there is a way to do this that accounts for current locale?
            long days = getDateDiff(start, end, TimeUnit.DAYS);
            // Add 1 since the trip starts at day zero.
            return (days + 1) + "d";
        }
        return "";
    }

    /**
     * Get a diff between two dates
     *
     * @param date1    the oldest date
     * @param date2    the newest date
     * @param timeUnit the unit in which you want the diff
     * @return the diff value, in the provided unit
     */
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillis = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillis, TimeUnit.MILLISECONDS);
    }

    private DateFormattingUtils() {
    }

    public static boolean datesOnSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
}
