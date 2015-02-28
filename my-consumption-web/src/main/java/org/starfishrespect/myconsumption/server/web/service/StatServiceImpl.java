package org.starfishrespect.myconsumption.server.web.service;

import org.starfishrespect.myconsumption.server.api.dto.StatsOverPeriodsDTO;
import org.starfishrespect.myconsumption.server.api.services.StatService;
import org.starfishrespect.myconsumption.server.business.controllers.StatController;
import org.starfishrespect.myconsumption.server.business.exception.DaoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

/**
 * Created by thibaud on 27.01.15.
 */
@Service("statService")
public class StatServiceImpl implements StatService {

    @Autowired
    private StatController statController;

    @Override
    public StatsOverPeriodsDTO getAllStats(String sensor) {
        try {
            return statController.getAllStats(sensor);
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

/*    @Override
    public Integer meanForSensor(String sensor) {
        try {
            return statController.getAverage(sensor);
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

    @Override
    public Integer maxForSensor(String sensor) {
        try {
            return statController.getMaxForSensor(sensor);
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

    @Override
    public Integer compDuration(String sensor, int duration) {
        try {
            return statController.getCompDurationForSensor(sensor, duration);
        } catch (DaoException e) {
            // only SENSOR_NOT_FOUND is possible for the moment
            switch (e.getExceptionType()) {
                case SENSOR_NOT_FOUND:
                    throw new NotFoundException();
                default:
                    throw new BadRequestException();
            }
        }
    }*/
}
