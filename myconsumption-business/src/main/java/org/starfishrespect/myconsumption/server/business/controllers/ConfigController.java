package org.starfishrespect.myconsumption.server.business.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest controller for the configurations.
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 * Author: Thibaud Ledent
 */
@RestController
@RequestMapping("/configs")
public class ConfigController {
    @RequestMapping(value = "/co2", method = RequestMethod.GET)
    public Double getkWhToCO2() {
        return 0.48; // 0,48 kg of CO2 per kWh
    }

    @RequestMapping(value = "/day", method = RequestMethod.GET)
    public Double getkWhDayPrice() {
        return 0.07; // €/kWh
    }

    @RequestMapping(value = "/night", method = RequestMethod.GET)
    public Double getkWhNightPrice() {
        return 0.0525; // €/kWh
    }
}
