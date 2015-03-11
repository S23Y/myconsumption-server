package org.starfishrespect.myconsumption.server.api.services;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.starfishrespect.myconsumption.server.api.dto.User;

/**
 * Created by thibaud on 11.03.15.
 */
@RestController
public class UserController {

    @RequestMapping("/user")
    public User getUserName(@RequestParam(value = "name") String name) {
        return new User(name);
    }
}