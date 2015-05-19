package org.starfishrespect.myconsumption.server.controllers;

import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.starfishrespect.myconsumption.server.api.dto.SimpleResponseDTO;
import org.starfishrespect.myconsumption.server.api.dto.UserDTO;
import org.starfishrespect.myconsumption.server.entities.User;
import org.starfishrespect.myconsumption.server.exceptions.DaoException;
import org.starfishrespect.myconsumption.server.repositories.SensorRepository;
import org.starfishrespect.myconsumption.server.repositories.UserRepository;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.security.Principal;
import java.util.List;

/**
 * Created by thibaud on 11.03.15.
 */
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserRepository mUserRepository;

    @Autowired
    private SensorRepository mSensorRepository;


    @RequestMapping(value = "/{name}", method = RequestMethod.GET)
    public UserDTO get(Principal principal, @PathVariable String name) {
        // Check if this user can access this resource
        if (!(principal.getName().equals(name)))
            throw new NotFoundException();

        User user = mUserRepository.getUser(name);

        if (user == null)
            throw new NotFoundException();
        else
            return new DozerBeanMapper().map(user, UserDTO.class);
    }

    @RequestMapping(value = "/{name}", method = RequestMethod.POST)
    public SimpleResponseDTO put(@PathVariable String name,
                                 @RequestParam(value = "password", defaultValue = "") String password)
            throws DaoException {

        if (mUserRepository.getUser(name) != null) {
            return new SimpleResponseDTO(SimpleResponseDTO.STATUS_ALREADY_EXISTS, "User already exists");
        }
        if (password.equals("")) {
            throw new BadRequestException(new Throwable("Password is empty"));
        }
        if (mUserRepository.insertUser(new User(name, password))) {
            return new SimpleResponseDTO(true, "user created");
        } else {
            return new SimpleResponseDTO(false, "Error while creating user");
        }
    }

    @RequestMapping(value = "/{name}/sensor/{sensorId}", method = RequestMethod.POST)
    public SimpleResponseDTO addSensor(@PathVariable String name, @PathVariable String sensorId)
            throws DaoException {

        User user = mUserRepository.getUser(name);

        if (user == null)
            throw new NotFoundException();

        if (!mSensorRepository.sensorExists(sensorId))
            throw new NotFoundException();

        if (user.getSensors().contains(sensorId))
            return new SimpleResponseDTO(false, "you already have this sensor");

        user.getSensors().add(sensorId);
        mUserRepository.updateUser(user);
        mSensorRepository.incrementUsageCount(sensorId);

        return new SimpleResponseDTO(true, "sensor associated to the user");
    }

    @RequestMapping(value = "/{name}", method = RequestMethod.DELETE)
    public SimpleResponseDTO deleteUser(@PathVariable String name) {
        mUserRepository.deleteUser(name);

        return new SimpleResponseDTO(true, "user deleted");
    }

    @RequestMapping(value = "/{name}/sensor/{sensorId}", method = RequestMethod.DELETE)
    public SimpleResponseDTO removeSensor(@PathVariable String name, @PathVariable String sensorId) throws DaoException {
        User user = mUserRepository.getUser(name);

        if (user == null)
            throw new NotFoundException();

        if (!user.getSensors().remove(sensorId))
            throw new NotFoundException();

        mUserRepository.updateUser(user);
        mSensorRepository.decrementUsageCount(sensorId);

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