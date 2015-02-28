package org.starfishrespect.myconsumption.server.web.service;

import org.starfishrespect.myconsumption.server.api.dto.SimpleResponseDTO;
import org.starfishrespect.myconsumption.server.api.dto.UserDTO;
import org.starfishrespect.myconsumption.server.api.services.UserService;
import org.starfishrespect.myconsumption.server.business.controllers.UsersController;
import org.starfishrespect.myconsumption.server.business.datamodel.User;
import org.starfishrespect.myconsumption.server.business.exception.DaoException;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

/**
 * Implementation of the user REST API
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private DozerBeanMapper dozerBeanMapper;
    @Autowired
    private UsersController usersController;

    @Override
    public UserDTO get(String username) {
        User user = usersController.get(username);
        if (user != null) {
            return dozerBeanMapper.map(user, UserDTO.class);
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    public SimpleResponseDTO put(String username, String password) {
        if (usersController.userExists(username)) {
            return new SimpleResponseDTO(SimpleResponseDTO.STATUS_ALREADY_EXISTS, "User already exists");
        }
        if (password.equals("")) {
            throw new BadRequestException(new Throwable("Password is empty"));
        }
        if (usersController.create(username, password)) {
            return new SimpleResponseDTO(true, "user created");
        } else {
            return new SimpleResponseDTO(false, "Error while creating user");
        }
    }

    @Override
    public SimpleResponseDTO addSensor(String username, String sensorId) {
        try {
            usersController.addSensor(username, sensorId);
            return new SimpleResponseDTO(true, "sensor associated to the user");
        } catch (DaoException e) {
            switch (e.getExceptionType()) {
                case USER_NOT_FOUND:
                case SENSOR_NOT_FOUND:
                    throw new NotFoundException();
                case SENSOR_EXISTS:
                    return new SimpleResponseDTO(false, "you already have this sensor");
                default:
                    return new SimpleResponseDTO(false, "error");
            }
        }
    }

    @Override
    public SimpleResponseDTO deleteUser(String username) {
        if (usersController.delete(username)) {
            return new SimpleResponseDTO(true, "user deleted");
        }
        throw new NotFoundException();
    }

    @Override
    public SimpleResponseDTO removeSensor(String username, String sensorId) {
        try {
            usersController.removeSensor(username, sensorId);
            return new SimpleResponseDTO(true, "sensor unassociated from the user");
        } catch (DaoException e) {
            switch (e.getExceptionType()) {
                case USER_NOT_FOUND:
                case SENSOR_NOT_FOUND:
                    throw new NotFoundException();
                default:
                    return new SimpleResponseDTO(false, "error");
            }
        }
    }

    @Override
    public SimpleResponseDTO pushToken(String username, String deviceType, String token) {
        if (deviceType == null || deviceType.equals("") || token == null || token.equals("")) {
            throw new BadRequestException();
        }
        try {
            usersController.addToken(username, deviceType, token);
            return new SimpleResponseDTO(true, "Token added");
        } catch (DaoException e) {
            switch (e.getExceptionType()) {
                case TOKEN_EXISTS:
                    return new SimpleResponseDTO(SimpleResponseDTO.STATUS_ALREADY_EXISTS, "Token already exists");
                default:
                    return new SimpleResponseDTO(false, "Impossible to update user");
            }
        }
    }

    @Override
    public SimpleResponseDTO deleteToken(String username, String token) {
        if (token.equals("")) {
            throw new BadRequestException();
        }
        try {
            usersController.deleteToken(username, token);
            return new SimpleResponseDTO(true, "Token removed");
        } catch (DaoException e) {
            e.printStackTrace();
            switch (e.getExceptionType()) {
                case USER_NOT_FOUND:
                case TOKEN_NOT_FOUND:
                    throw new NotFoundException();
                default:
                    return new SimpleResponseDTO(false, "Error when removing the token");
            }
        }
    }
}
