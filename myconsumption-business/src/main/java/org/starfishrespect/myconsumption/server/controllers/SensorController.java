package org.starfishrespect.myconsumption.server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.starfishrespect.myconsumption.server.api.dto.SensorDTO;
import org.starfishrespect.myconsumption.server.api.dto.SimpleResponseDTO;
import org.starfishrespect.myconsumption.server.api.dto.FluksoSensorSettingsDTO;
import org.starfishrespect.myconsumption.server.business.sensors.exceptions.RetrieveException;
import org.starfishrespect.myconsumption.server.business.sensors.flukso.FluksoRetriever;
import org.starfishrespect.myconsumption.server.business.sensors.flukso.FluksoSensor;
import org.starfishrespect.myconsumption.server.entities.MinuteValues;
import org.starfishrespect.myconsumption.server.entities.SensorDataset;
import org.starfishrespect.myconsumption.server.entities.Sensor;
import org.starfishrespect.myconsumption.server.entities.User;
import org.starfishrespect.myconsumption.server.exception.DaoException;
import org.starfishrespect.myconsumption.server.repositories.SensorRepository;
import org.starfishrespect.myconsumption.server.repositories.UserRepository;
import org.starfishrespect.myconsumption.server.repositories.ValuesRepository;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by thibaud on 11.03.15.
 */
@RestController
@RequestMapping("/sensors")
public class SensorController {

    @Autowired
    private SensorRepository mSensorRepository;

    @Autowired
    private ValuesRepository mValuesRepository;

    @Autowired
    private UserRepository mUserRepository;

    @RequestMapping(method = RequestMethod.GET)
    public List<SensorDTO> getAllSensors() {

        List<SensorDTO> result = new ArrayList<>();
        for (Sensor s : mSensorRepository.getAllSensors()) {
            result.add(new DozerBeanMapper().map(s, SensorDTO.class));
        }
        return result;
    }

    @RequestMapping(value = "/{sensorId}", method = RequestMethod.GET)
    public SensorDTO get(@PathVariable String sensorId) {
        Sensor sensor = mSensorRepository.getSensor(sensorId);

        if (sensor == null)
            throw new NotFoundException();

        return new DozerBeanMapper().map(sensor, SensorDTO.class);
    }

    /**
     * Returns the values from a given sensor
     */
    @RequestMapping(value = "/{sensorId}/data", method = RequestMethod.GET)
    public List<List<Integer>> valuesForSensor(@PathVariable String sensorId,
                               @RequestParam(value = "start", required = false, defaultValue = "0") int startTime,
                               @RequestParam(value = "end", required = false, defaultValue = "0") int endTime) throws DaoException {
        if (endTime == 0)
            endTime = Integer.MAX_VALUE;
        if (startTime < 0 || endTime < 0 || startTime > endTime) {
            throw new BadRequestException();
        }

        if (!mSensorRepository.sensorExists(sensorId))
            throw new NotFoundException();

        int effectiveStart = startTime - startTime % 3600;

        mValuesRepository.setSensor(sensorId);
        List<SensorDataset> daoValues = mValuesRepository.getSensor(new Date(((long) effectiveStart) * 1000L),
                new Date(((long) endTime) * 1000L));

        List<List<Integer>> values = new ArrayList<>();

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
                    List<Integer> item = new ArrayList<>();
                    item.add(time);
                    item.add(value.getValues().get(key).getValue(second));
                    values.add(item);
                }
            }
        }
        return values;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public SimpleResponseDTO addSensor(@RequestParam(value = "type", defaultValue = "") String sensorType,
                              @RequestParam(value = "settings", defaultValue = "") String settings,
                              @RequestParam(value = "name", defaultValue = "") String name,
                              @RequestParam(value = "user", defaultValue = "") String linkToUser) throws DaoException {

        if (sensorType.equals("") || settings.equals("") || name.equals("")) {
            throw new BadRequestException();
        }

        Sensor sensor = null;
        switch (sensorType) {
            case "flukso":
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    FluksoSensorSettingsDTO fluksoSettings = mapper.readValue(settings, FluksoSensorSettingsDTO.class);
                    sensor = new FluksoSensor(name, fluksoSettings.getFluksoId(), fluksoSettings.getToken());
                    // throws RetrieveException if settings are invalid
                    new FluksoRetriever((FluksoSensor) sensor);
                } catch (RetrieveException | IOException e) {
                    throw new BadRequestException();
                }
                break;
            default:
                throw new BadRequestException();
        }

        if (linkToUser.equals("")) {
            sensor = mSensorRepository.insertSensor(sensor);
        } else {
            User user = mUserRepository.getUser(linkToUser);
            if (user != null) {
                sensor = mSensorRepository.insertSensor(sensor);
                user.addSensor(sensor.getId());
                mUserRepository.updateUser(user);
                mSensorRepository.incrementUsageCount(sensor.getId());
            } else {
                throw new NotFoundException();
            }
        }
        return new SimpleResponseDTO(true, sensor.getId());
    }

    @RequestMapping(value = "/{sensorId}", method = RequestMethod.POST)
    public SimpleResponseDTO editSensor(@PathVariable String sensorId,
                                       @RequestParam(value = "name") String name,
                                       @RequestParam(value = "settings", defaultValue = "") String settings)
            throws DaoException {

        if (mSensorRepository.sensorExists(sensorId)) {
            Sensor sensor = mSensorRepository.getSensor(sensorId);
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
                            throw new BadRequestException();
                        }
                        break;
                    default:
                        throw new BadRequestException();
                }
                edited = true;
            }
            if (edited) {
                mSensorRepository.updateSensor(sensor);
            } else {
                return new SimpleResponseDTO(false, "Nothing changed");
            }
        } else {
            throw new NotFoundException();
        }
        return new SimpleResponseDTO(true, "Sensor edited");
    }

    @RequestMapping(value = "/", method = RequestMethod.DELETE)
    public SimpleResponseDTO clear() {
        /*
        for (Sensor s:mSensorRepository.getAllSensors()) {
            mSensorRepository.deleteSensor(s.getId());
        }
        return new SimpleResponseDTO(true, "database cleared");*/
        return new SimpleResponseDTO(false, "function disabled");
    }

    @RequestMapping(value = "/{sensorId}", method = RequestMethod.DELETE)
    public SimpleResponseDTO removeSensor(@PathVariable String sensorId) {
        if (mSensorRepository.sensorExists(sensorId)) {
            if (!mSensorRepository.deleteSensor(sensorId)) {
                return new SimpleResponseDTO(false, "error while deleting sensor");
            }
        } else {
            throw new NotFoundException();
        }
        return new SimpleResponseDTO(true, "sensor deleted");
    }
}
