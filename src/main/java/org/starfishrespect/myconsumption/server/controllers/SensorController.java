package org.starfishrespect.myconsumption.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.starfishrespect.myconsumption.server.entities.Sensor;
import org.starfishrespect.myconsumption.server.repositories.SensorRepository;

import javax.ws.rs.NotFoundException;
import java.util.List;

/**
 * Created by thibaud on 11.03.15.
 */
@RestController
@RequestMapping("/sensors")
public class SensorController {

    @Autowired
    private SensorRepository repository;

    @RequestMapping(method = RequestMethod.GET)
    public List<Sensor> getAllSensors() {
        return repository.findAll();
    }

    @RequestMapping(value = "/{sensor}", method = RequestMethod.GET)
    public Sensor get(@PathVariable String sensorId) {
        Sensor sensor = repository.findOne(sensorId);

        if (sensor == null)
            throw new NotFoundException();

        return sensor;
    }
    
 /*   *//**
     * Returns the values from a given sensor
     *
     * @param sensor
     * @param startTime
     * @param endTime
     * @return
     *//*
    @GET
    @Path("{sensor}/data")
    @Produces(MediaType.APPLICATION_JSON)
    public List<List<Integer>> valuesForSensor(@PathParam("sensor") String sensor,
                                               @QueryParam("start") @DefaultValue("0") int startTime,
                                               @QueryParam("end") @DefaultValue("0") int endTime);


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






 /*   @Override
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
