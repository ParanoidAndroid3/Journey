package com.paranoidandroid.journey.utils;

import com.paranoidandroid.journey.models.Leg;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class DateFormattingUtils {
    private static DateFormat DATE_FORMAT = SimpleDateFormat.getDateInstance(DateFormat.SHORT);

    public static String formatDuration(List<Leg> legs) {
        if (legs.size() > 0) {
            Date start = legs.get(0).getStartDate();
            Date end = legs.get(legs.size() - 1).getEndDate();
            if (start != null && end != null) {
                return String.format(Locale.US, "%s-%s",
                        DATE_FORMAT.format(start), DATE_FORMAT.format(end));
            }
        }
        return "";
    }

    private DateFormattingUtils() {
    }
}
