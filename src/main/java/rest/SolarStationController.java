package rest;

import models.SolarStation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.SolarStationService;

import java.util.Collection;

@RestController
@RequestMapping("api/solar")

public class SolarStationController {
    private final SolarStationService solarService;

    @Autowired
    public SolarStationController(SolarStationService solarService) {
        this.solarService = solarService;
    }

    @PostMapping
    public Collection<SolarStation> create(@RequestBody Collection<SolarStation> solarStations) {
        return solarService.create(solarStations);
    }
}
