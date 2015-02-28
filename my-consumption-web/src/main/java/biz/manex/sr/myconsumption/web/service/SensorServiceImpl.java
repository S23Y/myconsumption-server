package biz.manex.sr.myconsumption.web.service;

import biz.manex.sr.myconsumption.api.dto.SensorDTO;
import biz.manex.sr.myconsumption.api.dto.SimpleResponseDTO;
import biz.manex.sr.myconsumption.api.services.SensorService;
import biz.manex.sr.myconsumption.business.controllers.SensorController;
import biz.manex.sr.myconsumption.business.exception.DaoException;
import biz.manex.sr.myconsumption.business.sensors.Sensor;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Patrick Herbeuval on 16/04/14.
 */
@Service("sensorService")
public class SensorServiceImpl implements SensorService {

    @Autowired
    private SensorController sensorController;
    @Autowired
    private DozerBeanMapper dozerBeanMapper;

    @Override
    public List<SensorDTO> getAllSensors() {
        List<SensorDTO> result = new ArrayList<>();
        for (Sensor s : sensorController.getAll()) {
            result.add(dozerBeanMapper.map(s, SensorDTO.class));
        }
        return result;
    }

    @Override
    public SensorDTO get(String sensorId) {
        try {
            return dozerBeanMapper.map(sensorController.get(sensorId), SensorDTO.class);
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
    public List<List<Integer>> valuesForSensor(String sensor, int startTime, int endTime) {
        if (endTime == 0)
            endTime = Integer.MAX_VALUE;
        if (startTime < 0 || endTime < 0 || startTime > endTime) {
            throw new BadRequestException();
        }

        try {
            return sensorController.getValues(sensor, startTime, endTime);
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
    public SimpleResponseDTO editSensor(String sensorId, String name, String settings) {
        try {
            sensorController.editSensor(sensorId, name, settings);
            return new SimpleResponseDTO(true, "Sensor edited");
        } catch (DaoException e) {
            switch (e.getExceptionType()) {
                case SENSOR_NOT_FOUND:
                    throw new NotFoundException();
                case NOTHING_TO_EDIT:
                    return new SimpleResponseDTO(false, "Nothing changed");
                case UNKNOWN_SENSOR_TYPE:
                case CANNOT_CHANGE_SENSOR_TYPE:
                case BAD_FORMAT_SENSOR_SETTINGS:
                    throw new BadRequestException();
                default:
                    throw new BadRequestException();
            }
        }
    }

    @Override
    public SimpleResponseDTO addSensor(String sensorType, String settings, String name, String linkToUser) {
        if (sensorType.equals("") || settings.equals("") || name.equals("")) {
            throw new BadRequestException();
        }
        try {
            Sensor sensor = sensorController.addSensor(sensorType, settings, name, linkToUser);
            return new SimpleResponseDTO(true, sensor.getId());
        } catch (DaoException e) {
            switch (e.getExceptionType()) {
                case INVALID_SENSOR_SETTINGS:
                case BAD_FORMAT_SENSOR_SETTINGS:
                case UNKNOWN_SENSOR_TYPE:
                    throw new BadRequestException();
                case USER_NOT_FOUND:
                    throw new NotFoundException();
                default:
                    throw new BadRequestException();
            }
        }
    }

    @Override
    public SimpleResponseDTO removeSensor(String sensorId) {
        try {
            sensorController.removeSensor(sensorId);
        } catch (DaoException e) {
            switch (e.getExceptionType()) {
                case SENSOR_NOT_FOUND:
                    throw new NotFoundException();
                case DATABASE_OPERATION_ERROR:
                    return new SimpleResponseDTO(false, "error while deleting sensor");
                default:
                    throw new BadRequestException();
            }
        }
        return new SimpleResponseDTO(true, "sensor deleted");
    }

    @Override
    public SimpleResponseDTO clear() {
        /*
        for (Sensor s:sensorDao.getAllSensors()) {
            sensorDao.deleteSensor(s.getId());
        }
        return new SimpleResponseDTO(true, "database cleared");*/
        return new SimpleResponseDTO(false, "function disabled");
    }
}
