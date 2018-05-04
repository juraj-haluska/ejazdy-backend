package net.spacive.apps.ejazdybackend.config;

import com.amazonaws.util.DateUtils;

import java.util.Calendar;

public class Utils {

    public static Calendar parseISOString(String date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtils.parseISO8601Date(date));
        return calendar;
    }
}
