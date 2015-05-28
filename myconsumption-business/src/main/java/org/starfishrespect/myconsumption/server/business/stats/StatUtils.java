package org.starfishrespect.myconsumption.server.business.stats;

import org.starfishrespect.myconsumption.server.api.dto.Period;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Utils class for the stats
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 * Author: Thibaud Ledent
 */
public class StatUtils {
    private static final int DAY_START_AT = 7;
    private static final int NIGHT_START_AT = 22;

    /**
     * Set the given date to a Calendar at midnight.
     * For example, if we have Thursday 9th April at 11:00 PM, it will return Thursday 9th April at 00:00 AM
     * @return today's Calendar at midnight.
     */
    public static Calendar getCalendarAtMidnight(Date d) {
        Calendar cal = date2Calendar(d);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal;
    }

    /**
     * Number of loop needed for computeConsumptionDay function
     * @param p a Period
     * @return the number of loops
     */
    public static int getNumberOfDaysInPeriod(Period p) {
        switch (p) {
            case ALLTIME:
                return Integer.MAX_VALUE;
            case DAY:
                return 1;
            case WEEK:
                return Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_WEEK);
            case MONTH:
                return Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
            case YEAR:
                return Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_YEAR);
            default:
                return -1;
        }
    }

    /**
     * Converts a date to a unix timestamp in seconds.
     * @param date a Date to convert
     * @return a int corresponding to the Date in unix epoch timestamp (seconds)
     */
    public static int date2TimeStamp(Date date) {
        return (int) (date.getTime()/1000L);
    }

    /**
     * Converts a unix timestamp in seconds to a Date.
     * @param timestamp a timestamp to convert
     * @return a Date corresponding to the timestamp in unix epoch timestamp (seconds)
     */
    public static Date timestamp2Date(long timestamp) {
        return new Date(timestamp*1000);
    }

    /**
     * Set the given date at midnight.
     * For example, if we have Thursday 9th April at 11:00 PM, it will return Thursday 9th April at 00:00 AM
     * @param d a Date
     * @return today's Date at midnight
     */
    public static Date getDateAtMidnight(Date d) {
        // today
        Calendar date = date2Calendar(d);
        // reset hour, minutes, seconds and millis
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        return date.getTime();
    }

    public static Calendar timestamp2Calendar(long timestamp){
        Date date = timestamp2Date(timestamp);
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("Europe/Brussels"));
        cal.setTime(date);

        return cal;
    }

    public static boolean isDuringDayWeek(int timestamp) {
        Calendar date = timestamp2Calendar(timestamp);

        if (date.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || date.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
            return false;

        return (date.get(Calendar.HOUR_OF_DAY) >= DAY_START_AT) && (date.get(Calendar.HOUR_OF_DAY) < NIGHT_START_AT);
    }

    public static Calendar date2Calendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public static int calendar2TimeStamp(Calendar cal) {
        return (int) (cal.getTimeInMillis()/1000L);
    }
}
