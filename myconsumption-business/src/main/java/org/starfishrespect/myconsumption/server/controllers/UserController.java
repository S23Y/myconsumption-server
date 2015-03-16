package org.starfishrespect.myconsumption.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.starfishrespect.myconsumption.server.api.dto.SimpleResponseDTO;
import org.starfishrespect.myconsumption.server.entities.User;
import org.starfishrespect.myconsumption.server.exception.DaoException;
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
        User user = repository.getUser(name);

        if (user == null)
            throw new NotFoundException();
        else
            return user;

    }

    // TODO string password @request param ?
    @RequestMapping(value = "/{name}", method = RequestMethod.POST)
    public SimpleResponseDTO put(@PathVariable String name, String password) throws DaoException {
        if (repository.getUser(name) != null) {
            return new SimpleResponseDTO(SimpleResponseDTO.STATUS_ALREADY_EXISTS, "User already exists");
        }
        if (password.equals("")) {
            throw new BadRequestException(new Throwable("Password is empty"));
        }
        if (repository.updateUser(new User(name, password))) {
            return new SimpleResponseDTO(true, "user created");
        } else {
            return new SimpleResponseDTO(false, "Error while creating user");
        }
    }

    @RequestMapping(value = "/{name}/sensor/{sensorId}", method = RequestMethod.POST)
    public SimpleResponseDTO addSensor(@PathVariable String name, @PathVariable String sensorId) throws DaoException {
        User user = repository.getUser(name);

        if (user == null)
            throw new NotFoundException();

        // todo: check if sensor with this ID exists
        // NotFoundException() if it does not or:
        //  return new SimpleResponse(false, "you already have this sensor");

        user.addSensor(sensorId);
        repository.updateUser(user);
        // todo: sensorDao.incrementUsageCount(sensorId);

        return new SimpleResponseDTO(true, "sensor associated to the user");
    }

    @RequestMapping(value = "/{name}", method = RequestMethod.DELETE)
    public SimpleResponseDTO deleteUser(@PathVariable String name) {
        repository.deleteUser(name);

        return new SimpleResponseDTO(true, "user deleted");
    }

    @RequestMapping(value = "/{name}/sensor/{sensorId}", method = RequestMethod.DELETE)
    public SimpleResponseDTO removeSensor(@PathVariable String name, @PathVariable String sensorId) throws DaoException {
        User user = repository.getUser(name);

        if (user == null)
            throw new NotFoundException();

        // todo: check if sensor with this ID exists
        // NotFoundException() if it does not or:
        //  return new SimpleResponse(false, "you already have this sensor");

        user.removeSensor(sensorId);
        repository.updateUser(user);
        //sensorDao.decrementUsageCount(sensorId);

        return new SimpleResponseDTO(true, "sensor unassociated from the user");
    }


    // TODO
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

// TODO
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