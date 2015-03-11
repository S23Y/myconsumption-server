package org.starfishrespect.myconsumption.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.starfishrespect.myconsumption.server.SimpleResponse;
import org.starfishrespect.myconsumption.server.entities.User;
import org.starfishrespect.myconsumption.server.repositories.UserRepository;

import javax.ws.rs.BadRequestException;

/**
 * Created by thibaud on 11.03.15.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository repository;

    @RequestMapping(method = RequestMethod.GET)
    public User get(@RequestParam(value = "name") String name) {
        User user = repository.findByName(name);

        if (user == null)
            throw new IllegalArgumentException("Cannot find user with name: " + name);
        else
            return user;

    }

    @RequestMapping(method = RequestMethod.POST)
    public SimpleResponse put(@RequestParam(value = "name") String name, String password) {
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

}