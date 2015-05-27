package org.starfishrespect.myconsumption.server.business.controllers;

import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.starfishrespect.myconsumption.server.api.dto.SimpleResponseDTO;
import org.starfishrespect.myconsumption.server.api.dto.UserDTO;
import org.starfishrespect.myconsumption.server.business.entities.User;
import org.starfishrespect.myconsumption.server.business.exceptions.DaoException;
import org.starfishrespect.myconsumption.server.business.repositories.SensorRepository;
import org.starfishrespect.myconsumption.server.business.repositories.UserRepository;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.security.Principal;

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
    public SimpleResponseDTO addSensor(Principal principal,
                                       @PathVariable String name, @PathVariable String sensorId)
            throws DaoException {

        // Check if this user can access this resource
        if (!(principal.getName().equals(name)))
            return new SimpleResponseDTO(false, "you are not allowed to modify this user");

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
    public SimpleResponseDTO deleteUser(Principal principal, @PathVariable String name) {

        // Check if this user can access this resource
        if (!(principal.getName().equals(name)))
            return new SimpleResponseDTO(false, "you are not allowed to delete this user");

        mUserRepository.deleteUser(name);

        return new SimpleResponseDTO(true, "user deleted");
    }

    @RequestMapping(value = "/{name}/sensor/{sensorId}", method = RequestMethod.DELETE)
    public SimpleResponseDTO removeSensor(Principal principal,
                                          @PathVariable String name, @PathVariable String sensorId) throws DaoException {

        // Check if this user can access this resource
        if (!(principal.getName().equals(name)))
            return new SimpleResponseDTO(false, "you are not allowed to modify this user");

        User user = mUserRepository.getUser(name);

        if (user == null)
            throw new NotFoundException();

        if (!user.getSensors().remove(sensorId))
            throw new NotFoundException();

        mUserRepository.updateUser(user);
        mSensorRepository.decrementUsageCount(sensorId);

        return new SimpleResponseDTO(true, "sensor unassociated from the user");
    }

}