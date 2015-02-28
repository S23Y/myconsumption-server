package org.starfishrespect.myconsumption.server.business.controllers;

import org.starfishrespect.myconsumption.server.api.dto.StatsOverPeriodsDTO;
import org.starfishrespect.myconsumption.server.api.dto.ExtremumDTO;
import org.starfishrespect.myconsumption.server.api.dto.Period;
import org.starfishrespect.myconsumption.server.api.dto.StatDTO;
import org.starfishrespect.myconsumption.server.business.exception.DaoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by thibaud on 18.02.15.
 */
@Component
public class StatController {
    @Autowired
    private SensorController sensorController;

    public StatsOverPeriodsDTO getAllStats(String sensor) throws DaoException {
        List<StatDTO> statDTOs = new ArrayList<>();

        for (Period p : Period.values()) {
            StatDTO statDTO = new StatDTO(p);

/*            System.out.println("Period " + p.toString() + "\n" +
                    "Begin: " + new java.util.Date((long)getStartTime(p)*1000).toString() + "\n" +
                    "End: " + new java.util.Date((long)getEndTime()*1000).toString() + "\n");*/

            List<List<Integer>> lValues = sensorController.getValues(sensor, getStartTime(p), getEndTime());
            statDTO.setAverage(getAverage(lValues));
            statDTO.setMin(getMin(lValues));
            statDTO.setMax(getMax(lValues));
            statDTO.setConsumption(getConsumption(lValues));

            List<List<Integer>> lOldValues = sensorController.getValues(sensor, getStartTime(p, 2), getStartTime(p));
            statDTO.setDiffLastTwo(getDiff(lValues, lOldValues));

            statDTOs.add(statDTO);
        }

        StatsOverPeriodsDTO allStats = new StatsOverPeriodsDTO(sensor);
        allStats.setStatDTOs(statDTOs);
        return allStats;
    }

    private int getStartTime(Period p) {
        return getStartTime(p, 1);
    }

    /**
     * Get the unix timestamp at which we should begin the value retrieval
     * @param period a Period
     * @param m an int used as a multiplier
     * @return
     */
    private int getStartTime(Period period, int m) {
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

    private int getEndTime() {
        return (int) (System.currentTimeMillis() / 1000L);
    }


    public Integer getAverage(List<List<Integer>> lValues) {
        if (lValues.size() == 0)
            return null;

        int sum = 0;
        
        for(List<Integer> l : lValues)
            sum += l.get(1);

        return sum/lValues.size();
    }

    public ExtremumDTO getMax(List<List<Integer>> lValues) {
        if (lValues.size() == 0)
            return null;

        int max = 0;
        int timestamp = 0;

        for (List<Integer> l : lValues) {
            if (l.get(1) > max) {
                timestamp = l.get(0);
                max = l.get(1);
            }
        }

        return new ExtremumDTO(timestamp, max);
    }

    public ExtremumDTO getMin(List<List<Integer>> lValues) {
        if (lValues.size() == 0)
            return null;

        int min = Integer.MAX_VALUE;
        int timestamp = 0;

        for (List<Integer> l : lValues) {
            if (l.get(1) < min) {
                timestamp = l.get(0);
                min = l.get(1);
            }
        }

        return new ExtremumDTO(timestamp, min);
    }

    public Integer getConsumption(List<List<Integer>> lValues) {
        if (lValues.size() == 0)
            return null;

        int total = 0;

        for (List<Integer> l : lValues)
            total += l.get(1);

        return total;
    }

    private Integer getDiff(List<List<Integer>> lValues, List<List<Integer>> lOldValues) {
        if (lValues.size() == 0 || lOldValues.size() == 0)
            return null;

        return getConsumption(lValues) - getConsumption(lOldValues);
    }

}
