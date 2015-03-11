package org.starfishrespect.myconsumption.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.starfishrespect.myconsumption.server.SimpleResponse;
import org.starfishrespect.myconsumption.server.entities.User;
import org.starfishrespect.myconsumption.server.repositories.UserRepository;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

/**
 * Created by thibaud on 11.03.15.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository repository;

    @RequestMapping(value = "/{name}", method = RequestMethod.GET)
    public User get(@PathVariable String name) {
        User user = repository.findByName(name);

        if (user == null)
            throw new NotFoundException();
        else
            return user;

    }

    @RequestMapping(value = "/{name}", method = RequestMethod.POST)
    public SimpleResponse put(@PathVariable String name, String password) {
        if (repository.findByName(name) != null) {
            return new SimpleResponse(SimpleResponse.STATUS_ALREADY_EXISTS, "User already exists");
        }
        if (password.equals("")) {
            throw new BadRequestException(new Throwable("Password is empty"));
        }
        if (repository.save(new User(name, password)) != null) {
            return new SimpleResponse(true, "user created");
        } else {
            return new SimpleResponse(false, "Error while creating user");
        }
    }

    @RequestMapping(value = "/{name}/sensor/{sensorId}", method = RequestMethod.POST)
    public SimpleResponse addSensor(@PathVariable String name, @PathVariable String sensorId) {
        User user = repository.findByName(name);

        if (user == null)
            throw new NotFoundException();

        // todo: check if sensor with this ID exists
        // NotFoundException() if it does not or:
        //  return new SimpleResponse(false, "you already have this sensor");

        user.addSensor(sensorId);
        repository.save(user);
        // todo: sensorDao.incrementUsageCount(sensorId);

        return new SimpleResponse(true, "sensor associated to the user");
    }

}