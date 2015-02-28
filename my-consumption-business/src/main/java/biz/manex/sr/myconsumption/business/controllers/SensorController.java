package biz.manex.sr.myconsumption.business.controllers;

import biz.manex.sr.myconsumption.api.dto.FluksoSensorSettingsDTO;
import biz.manex.sr.myconsumption.business.dao.SensorDao;
import biz.manex.sr.myconsumption.business.dao.UsersDao;
import biz.manex.sr.myconsumption.business.dao.ValuesDao;
import biz.manex.sr.myconsumption.business.datamodel.MinuteValues;
import biz.manex.sr.myconsumption.business.datamodel.SensorDataset;
import biz.manex.sr.myconsumption.business.datamodel.User;
import biz.manex.sr.myconsumption.business.exception.DaoException;
import biz.manex.sr.myconsumption.business.exception.ExceptionType;
import biz.manex.sr.myconsumption.business.sensors.Sensor;
import biz.manex.sr.myconsumption.business.sensors.exceptions.RetrieveException;
import biz.manex.sr.myconsumption.business.sensors.flukso.FluksoRetriever;
import biz.manex.sr.myconsumption.business.sensors.flukso.FluksoSensor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

/**
 * Controller for sensors
 */

@Component
public class SensorController {
    @Autowired
    private UsersDao usersDao;
    @Autowired
    private SensorDao sensorDao;
    @Autowired
    private ValuesDao valuesDao;

    public List<Sensor> getAll() {
        return sensorDao.getAllSensors();
    }

    public Sensor get(String sensorId) throws DaoException {
        Sensor sensor = sensorDao.getSensor(sensorId);
        if (sensor == null) {
            throw new DaoException(ExceptionType.SENSOR_NOT_FOUND);
        }
        return sensor;
    }

    public List<List<Integer>> getValues(String sensor, int startTime, int endTime) throws DaoException {
        if (endTime == 0) {
            endTime = Integer.MAX_VALUE;
        }
        if (!sensorDao.sensorExists(sensor)) {
            throw new DaoException(ExceptionType.SENSOR_NOT_FOUND);
        }
        valuesDao.setSensor(sensor);

        int effectiveStart = startTime - startTime % 3600;
        List<SensorDataset> daoValues = valuesDao.getSensor(new Date(((long) effectiveStart) * 1000L),
                new Date(((long) endTime) * 1000L));
        List<List<Integer>> values = new ArrayList<List<Integer>>();
        for (SensorDataset value : daoValues) {
            int start = (int) (value.getTimestamp().getTime() / 1000);
            TreeMap<Integer, MinuteValues> v = value.getValues();
            if (v == null) {
                continue;
            }
            for (int key : v.keySet()) {
                for (int second : v.get(key).containedSeconds()) {
                    int time = start + key * 60 + second;
                    if (time < startTime || time > endTime) {
                        continue;
                    }
                    List<Integer> item = new ArrayList<Integer>();
                    item.add(time);
                    item.add(value.getValues().get(key).getValue(second));
                    values.add(item);
                }
            }
        }
        return values;
    }

    public Sensor addSensor(String sensorType, String settings, String name, String linkToUser) throws DaoException {
        Sensor sensor = null;
        switch (sensorType) {
            case "flukso":
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    FluksoSensorSettingsDTO fluksoSettings = mapper.readValue(settings, FluksoSensorSettingsDTO.class);
                    sensor = new FluksoSensor(name, fluksoSettings.getFluksoId(), fluksoSettings.getToken());
                    // throws RetrieveException if settings are invalid
                    new FluksoRetriever((FluksoSensor) sensor);
                } catch (RetrieveException e) {
                    throw new DaoException(ExceptionType.INVALID_SENSOR_SETTINGS);
                } catch (IOException e) {
                    throw new DaoException(ExceptionType.BAD_FORMAT_SENSOR_SETTINGS);
                }
                break;
            default:
                throw new DaoException(ExceptionType.UNKNOWN_SENSOR_TYPE);
        }

        if (linkToUser.equals("")) {
            sensor = sensorDao.insertSensor(sensor);
            return sensor;
        } else {
            User user = usersDao.getUser(linkToUser);
            if (user != null) {
                sensor = sensorDao.insertSensor(sensor);
                user.addSensor(sensor.getId());
                usersDao.updateUser(user);
                sensorDao.incrementUsageCount(sensor.getId());
                return sensor;
            } else {
                throw new DaoException(ExceptionType.USER_NOT_FOUND);
            }
        }
    }

    public void editSensor(String sensorId, String name, String settings) throws DaoException {
        if (sensorDao.sensorExists(sensorId)) {
            Sensor sensor = sensorDao.getSensor(sensorId);
            boolean edited = false;
            if (name != null && !name.equals("")) {
                sensor.setName(name);
                edited = true;
            }
            if (settings != null && !settings.equals("")) {
                switch (sensor.getType()) {
                    case "flukso":
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            FluksoSensorSettingsDTO fluksoSettings = mapper.readValue(settings, FluksoSensorSettingsDTO.class);
                            if (fluksoSettings.getToken() != null && !fluksoSettings.getToken().equals(""))
                                ((FluksoSensor) sensor).setToken(fluksoSettings.getToken());
                        } catch (IOException e) {
                            throw new DaoException(ExceptionType.BAD_FORMAT_SENSOR_SETTINGS);
                        }
                        break;
                    default:
                        throw new DaoException(ExceptionType.UNKNOWN_SENSOR_TYPE);
                }
                edited = true;
            }
            if (edited) {
                sensorDao.updateSensor(sensor);
            } else {
                throw new DaoException(ExceptionType.NOTHING_TO_EDIT);
            }
        } else {
            throw new DaoException(ExceptionType.SENSOR_NOT_FOUND);
        }
    }

    public void removeSensor(String sensorId) throws DaoException {
        if (sensorDao.sensorExists(sensorId)) {
            if (!sensorDao.deleteSensor(sensorId)) {
                throw new DaoException(ExceptionType.DATABASE_OPERATION_ERROR);
            }
        } else {
            throw new DaoException(ExceptionType.SENSOR_NOT_FOUND);
        }
    }
}
