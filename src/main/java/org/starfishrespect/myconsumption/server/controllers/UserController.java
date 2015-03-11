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

    @RequestMapping(value = "/{name}", method = RequestMethod.DELETE)
    public SimpleResponse deleteUser(@PathVariable String name) {
        User user = repository.findByName(name);

        if (user == null)
            throw new NotFoundException();

        repository.delete(user);

        return new SimpleResponse(true, "user deleted");
    }

    @RequestMapping(value = "/{name}/sensor/{sensorId}", method = RequestMethod.DELETE)
    public SimpleResponse removeSensor(@PathVariable String name, @PathVariable String sensorId) {
        User user = repository.findByName(name);

        if (user == null)
            throw new NotFoundException();

        // todo: check if sensor with this ID exists
        // NotFoundException() if it does not or:
        //  return new SimpleResponse(false, "you already have this sensor");

        user.removeSensor(sensorId);
        repository.save(user);
        //sensorDao.decrementUsageCount(sensorId);

        return new SimpleResponse(true, "sensor unassociated from the user");
    }


/*    @Override
    public SimpleResponse pushToken(String username, String deviceType, String token) {
        if (deviceType == null || deviceType.equals("") || token == null || token.equals("")) {
            throw new BadRequestException();
        }
        try {
            usersController.addToken(username, deviceType, token);
            return new SimpleResponse(true, "Token added");
        } catch (DaoException e) {
            switch (e.getExceptionType()) {
                case TOKEN_EXISTS:
                    return new SimpleResponse(SimpleResponse.STATUS_ALREADY_EXISTS, "Token already exists");
                default:
                    return new SimpleResponse(false, "Impossible to update user");
            }
        }
    }

    @Override
    public SimpleResponse deleteToken(String username, String token) {
        if (token.equals("")) {
            throw new BadRequestException();
        }
        try {
            usersController.deleteToken(username, token);
            return new SimpleResponse(true, "Token removed");
        } catch (DaoException e) {
            e.printStackTrace();
            switch (e.getExceptionType()) {
                case USER_NOT_FOUND:
                case TOKEN_NOT_FOUND:
                    throw new NotFoundException();
                default:
                    return new SimpleResponse(false, "Error when removing the token");
            }
        }
    }*/

}