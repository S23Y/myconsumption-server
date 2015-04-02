package org.starfishrespect.myconsumption.server.controllers;

/**
 * Created by thibaud on 30.03.15.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.starfishrespect.myconsumption.server.api.dto.*;
import org.starfishrespect.myconsumption.server.exceptions.DaoException;
import org.starfishrespect.myconsumption.server.repositories.StatRepository;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.*;


/**
 * Created by thibaud on 11.03.15.
 */
@RestController
@RequestMapping("/stats")
public class StatController {

    @Autowired
    private StatRepository mStatRepository;

    @Autowired
    private SensorController mSensorController;

    @RequestMapping(value = "/sensor/{sensorId}", method = RequestMethod.GET)
    public StatsOverPeriodsDTO getAllStats(@PathVariable String sensorId) {
        try {
            return computeAllStats(sensorId);
        } catch (DaoException e) {
            // only SENSOR_NOT_FOUND is possible for the moment
            switch (e.getExceptionType()) {
                case SENSOR_NOT_FOUND:
                    throw new NotFoundException();
                default:
                    throw new BadRequestException();
            }
        }
    }

    private StatsOverPeriodsDTO computeAllStats(String sensor) throws DaoException {
        List<StatDTO> statDTOs = new ArrayList<>();

        for (Period p : Period.values()) {
            StatDTO statDTO = new StatDTO(p);

/*            System.out.println("Period " + p.toString() + "\n" +
                    "Begin: " + new java.util.Date((long)getStartTime(p)*1000).toString() + "\n" +
                    "End: " + new java.util.Date((long)getEndTime()*1000).toString() + "\n");*/

            List<List<Integer>> lValues = mSensorController.getValues(sensor, getStartTime(p), getEndTime());
            statDTO.setAverage(getAverage(lValues));
            statDTO.setMin(getMin(lValues));
            statDTO.setMax(getMax(lValues));
            statDTO.setConsumption(getConsumption(lValues));

            List<List<Integer>> lOldValues = mSensorController.getValues(sensor, getStartTime(p, 2), getStartTime(p));
            statDTO.setDiffLastTwo(getDiff(lValues, lOldValues));

            statDTOs.add(statDTO);
        }

        StatsOverPeriodsDTO allStats = new StatsOverPeriodsDTO();
        allStats.setSensorId(sensor);
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
