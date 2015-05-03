package org.starfishrespect.myconsumption.server.entities;

import org.starfishrespect.myconsumption.server.api.dto.Period;

import java.util.Calendar;

/**
 * Created by thibaud on 03.05.15.
 */
public class PeriodStat {
    private Period period;
    private Calendar beginingOfPeriod;


    public int getNumberOfDaysInPeriod() {
        switch (period) {
            case ALLTIME:
                return (int) (Calendar.getInstance().getTimeInMillis() - beginingOfPeriod.getTimeInMillis()) / (1000 * 60 * 60 * 24);
            case DAY:
                return 1;
            case WEEK:
                return beginingOfPeriod.getActualMaximum(Calendar.DAY_OF_WEEK);
            case MONTH:
                return beginingOfPeriod.getActualMaximum(Calendar.DAY_OF_MONTH);
            case YEAR:
                return beginingOfPeriod.getActualMaximum(Calendar.DAY_OF_YEAR);
            default:
                return -1;
        }
    }
}
