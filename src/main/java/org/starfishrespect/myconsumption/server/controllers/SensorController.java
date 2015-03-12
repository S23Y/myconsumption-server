package org.starfishrespect.myconsumption.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.starfishrespect.myconsumption.server.entities.SensorDataset;
import org.starfishrespect.myconsumption.server.entities.Sensor;
import org.starfishrespect.myconsumption.server.repositories.SensorRepository;
import org.starfishrespect.myconsumption.server.repositories.ValuesRepository;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
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

    @RequestMapping(method = RequestMethod.GET)
    public List<Sensor> getAllSensors() {
        return mSensorRepository.findAll();
    }

    @RequestMapping(value = "/{sensorId}", method = RequestMethod.GET)
    public Sensor get(@PathVariable String sensorId) {
        Sensor sensor = mSensorRepository.findOne(sensorId);

        if (sensor == null)
            throw new NotFoundException();

        return sensor;
    }

    /**
     * Returns the values from a given sensor
     */
    @RequestMapping(value = "/{sensorId}/data", method = RequestMethod.GET)
    public List<List<Integer>> valuesForSensor(@PathVariable String sensorId,
                                               @RequestParam(value = "start", required = false, defaultValue = "0") int startTime,
                                               @RequestParam(value = "end", required = false, defaultValue = "0") int endTime) {
        if (endTime == 0)
            endTime = Integer.MAX_VALUE;
        if (startTime < 0 || endTime < 0 || startTime > endTime) {
            throw new BadRequestException();
        }

        Sensor sensor = mSensorRepository.findOne(sensorId);

        if (sensor == null)
            throw new NotFoundException();

        int effectiveStart = startTime - startTime % 3600;
        List<SensorDataset> values = mValuesRepository.getValuesForSensor(sensorId,
                new Date(((long) effectiveStart) * 1000L), new Date(((long) endTime) * 1000L));

        return null;

/*        valuesDao.setSensor(sensor);


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
        return values;*/


    }

 /*


    @POST
    @Path("{sensor}")
    @Produces(MediaType.APPLICATION_JSON)
    public SimpleResponseDTO editSensor(@PathParam("sensor") String sensorId,
                                        @FormParam("name") String name,
                                        @FormParam("settings") @DefaultValue("") String settings);

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public SimpleResponseDTO addSensor(@FormParam("type") @DefaultValue("") String sensorType,
                                       @FormParam("settings") @DefaultValue("") String settings,
                                       @FormParam("name") @DefaultValue("") String name,
                                       @FormParam("user") @DefaultValue("") String linkToUser);

    @DELETE
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public SimpleResponseDTO clear();

    @DELETE
    @Path("{sensor}")
    @Produces(MediaType.APPLICATION_JSON)
    public SimpleResponseDTO removeSensor(@PathParam("sensor") String sensorId);
    
    */






 /*

    @Override
    public SimpleResponse editSensor(String sensorId, String name, String settings) {
        try {
            sensorController.editSensor(sensorId, name, settings);
            return new SimpleResponse(true, "Sensor edited");
        } catch (DaoException e) {
            switch (e.getExceptionType()) {
                case SENSOR_NOT_FOUND:
                    throw new NotFoundException();
                case NOTHING_TO_EDIT:
                    return new SimpleResponse(false, "Nothing changed");
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
    public SimpleResponse addSensor(String sensorType, String settings, String name, String linkToUser) {
        if (sensorType.equals("") || settings.equals("") || name.equals("")) {
            throw new BadRequestException();
        }
        try {
            Sensor sensor = sensorController.addSensor(sensorType, settings, name, linkToUser);
            return new SimpleResponse(true, sensor.getId());
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
    public SimpleResponse removeSensor(String sensorId) {
        try {
            sensorController.removeSensor(sensorId);
        } catch (DaoException e) {
            switch (e.getExceptionType()) {
                case SENSOR_NOT_FOUND:
                    throw new NotFoundException();
                case DATABASE_OPERATION_ERROR:
                    return new SimpleResponse(false, "error while deleting sensor");
                default:
                    throw new BadRequestException();
            }
        }
        return new SimpleResponse(true, "sensor deleted");
    }

    @Override
    public SimpleResponse clear() {
        *//*
        for (Sensor s:sensorDao.getAllSensors()) {
            sensorDao.deleteSensor(s.getId());
        }
        return new SimpleResponse(true, "database cleared");*//*
        return new SimpleResponse(false, "function disabled");
    }*/
}
