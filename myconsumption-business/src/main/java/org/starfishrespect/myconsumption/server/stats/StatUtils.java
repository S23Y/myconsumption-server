package org.starfishrespect.myconsumption.server.stats;

import org.starfishrespect.myconsumption.server.api.dto.Period;
import org.starfishrespect.myconsumption.server.entities.Sensor;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by thibaud on 26.04.15.
 */
public class StatUtils {

    private static Date sDateAtMidnight;

    /**
     * Get the unix timestamp at which we should begin the value retrieval
     * @param p a Period
     * @return the unix timestamp at which we should begin the value retrieval
     */
    public static int getStartTime(Period p) {
        return getStartTime(p, 1);
    }

    /**
     * Get the unix timestamp at which we should begin the value retrieval
     * @param period a Period
     * @param m an int used as a multiplier
     * @return the unix timestamp at which we should begin the value retrieval
     */
    public static int getStartTime(Period period, int m) {
        int day = 86400; // 86400 = one day in Unix Timestamp

        switch (period) {
            case ALLTIME:
                return 0;
            case DAY:
                return (int) (System.currentTimeMillis() / 1000L) - (day * m);
            case WEEK:
                return (int) (System.currentTimeMillis() / 1000L) - (day * 7 * m);
            case MONTH:
                return (int) (System.currentTimeMillis() / 1000L)
                        - (day * Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH) * m);
            case YEAR:
                return (int) (System.currentTimeMillis() / 1000L)
                        - (day * Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_YEAR) * m);
            default:
                return -1;
        }
    }

    /**
     * Get the unix timestamp at which we should end the value retrieval
     * @return current system UNIX timestamp
     */
    public static int getEndTime() {
        return (int) (System.currentTimeMillis() / 1000L);
    }

    /**
     * Get today's date but at a given hour.
     * @param hour the hour to set
     * @return today's date calendar at a given hour.
     */
    public static Calendar getCalendar(int hour) {
        //DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar date = new GregorianCalendar(); // get current time
        //System.out.println(dateFormat.format(date.getTime())); //2014/08/06 16:00:22
        date.set(Calendar.HOUR_OF_DAY, hour);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        //System.out.println(dateFormat.format(date.getTime())); //2014/08/06 16:00:22

        // If the hour we've just set is in the future...
        if (date.after(new GregorianCalendar()))
            date.add(Calendar.DAY_OF_MONTH, -1); // ...decrement by one day

        //System.out.println(dateFormat.format(date.getTime())); //2014/08/06 16:00:22

        return date;
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

    public static double getMultiplierForPeriod(int duration, Sensor sensor) {
        System.out.println(duration);
        return 1;
//        switch (duration) {
//            case ALLTIME:
//                return date2TimeStamp(sensor.getLastValue()) - date2TimeStamp(sensor.getFirstValue());
//            case DAY:
//                return 60 * 24;
//            case WEEK:
//                return 60 * 24 * 7;
//            case MONTH:
//                return 60 * 24 * Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
//            case YEAR:
//                return 60 * 24 * Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_YEAR);
//            default:
//                return -1;
//        }
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
     * Get today's date at midnight.
     * For example, if we are on Thursday 9th April at 11:00 PM, it will return Thursday 9th April at 00:00 AM
     * @return today's Date at midnight
     */
    public static Date getDateAtMidnight() {
        // today
        Calendar date = new GregorianCalendar();
        // reset hour, minutes, seconds and millis
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        return date.getTime();
    }
}
