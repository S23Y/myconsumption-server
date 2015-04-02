package org.starfishrespect.myconsumption.server.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by thibaud on 02.04.15.
 */
@RestController
@RequestMapping("/config")
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
