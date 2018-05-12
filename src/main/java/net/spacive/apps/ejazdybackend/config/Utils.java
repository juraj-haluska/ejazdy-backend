package net.spacive.apps.ejazdybackend.config;

import com.amazonaws.util.DateUtils;

import java.util.Calendar;

/**
 * Handy utility class.
 *
 * <p>Contains only simple, short static methods.
 *
 * @author  Juraj Haluska
 */
public class Utils {

    /**
     * Parse date formated in ISO string to calendar.
     *
     * @param date contains ISO8601 date string
     * @return new instance of calendar
     */
    public static Calendar parseISOString(String date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtils.parseISO8601Date(date));
        return calendar;
    }
}
