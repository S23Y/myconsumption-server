package org.starfishrespect.myconsumption.server.api.services;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.starfishrespect.myconsumption.server.api.dto.UserDTO;

/**
 * Created by thibaud on 11.03.15.
 */
@RestController
public class UserController {

    @RequestMapping("/user")
    public UserDTO getUserName(@RequestParam(value = "name") String name) {
        return new UserDTO(name);
    }
}