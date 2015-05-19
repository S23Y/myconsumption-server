package org.starfishrespect.myconsumption.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.starfishrespect.myconsumption.server.api.dto.SimpleResponseDTO;
import org.starfishrespect.myconsumption.server.entities.User;
import org.starfishrespect.myconsumption.server.exceptions.DaoException;
import org.starfishrespect.myconsumption.server.repositories.UserRepository;

import javax.ws.rs.NotFoundException;
import java.security.Principal;

/**
 * Created by thibaud on 08.05.15.
 */
@RestController
@RequestMapping("/notifs")
public class NotifController {

    @Autowired
    private UserRepository mUserRepository;

    @RequestMapping(value = "/{name}/id/{registerId}", method = RequestMethod.POST)
    public SimpleResponseDTO registerId(Principal principal,
                                        @PathVariable String name, @PathVariable String registerId)
            throws DaoException {

        // Check if this user can access this resource
        if (!(principal.getName().equals(name)))
            return new SimpleResponseDTO(false, "you are not allowed to modify this user");

        User user = mUserRepository.getUser(name);

        if (user == null)
            throw new NotFoundException();

        if (registerId == null || registerId.isEmpty())
            return new SimpleResponseDTO(false, "Register id invalid");

        // Set or override current id
        user.setRegisterId(registerId);
        mUserRepository.updateUser(user);

        return new SimpleResponseDTO(true, "Register id associated to the user");
    }
}
