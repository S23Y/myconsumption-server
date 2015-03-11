package org.starfishrespect.myconsumption.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.starfishrespect.myconsumption.server.entities.User;
import org.starfishrespect.myconsumption.server.repositories.UserRepository;

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
        return repository.findByName(name);
    }

    @RequestMapping(method = RequestMethod.POST)
    public User put(@RequestParam(value = "name") String name) {
        if (repository.findByName(name) != null)
            return null; // user already exists

        return repository.save(new User(name));
    }

}