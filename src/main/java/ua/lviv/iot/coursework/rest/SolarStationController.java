package ua.lviv.iot.coursework.rest;

import ua.lviv.iot.coursework.models.SolarStation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.lviv.iot.coursework.service.SolarStationService;

import java.util.Collection;

@RestController
@RequestMapping("api/solar")

public record SolarStationController(SolarStationService solarService) {
    @Autowired
    public SolarStationController {
    }

    @PostMapping
    public Collection<SolarStation> create(@RequestBody Collection<SolarStation> solarStations) {
        return solarService.create(solarStations);
    }
}
