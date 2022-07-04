package ua.lviv.iot.coursework.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ua.lviv.iot.coursework.models.SolarStation;
import ua.lviv.iot.coursework.service.SolarStationService;
import java.util.Collection;

@RestController
@RequestMapping("api/solar")
public class SolarStationController {
    private final SolarStationService solarStationService;

    @Autowired
    public SolarStationController(SolarStationService solarStationService) {
        this.solarStationService = solarStationService;
    }

    @PostMapping
    public final Collection<SolarStation> create(@RequestBody Collection<SolarStation> solars) throws Exception {
        return solarStationService.create(solars);
    }

    @GetMapping("/{id}")
    public final SolarStation getById(@PathVariable final Integer id) {
        return solarStationService.getById(id);
    }

    @GetMapping
    public final Collection<SolarStation> getAll() {
        return solarStationService.getAll();
    }

    @DeleteMapping("/{stationId}")
    public final void deleteById(@PathVariable final Integer stationId) throws Exception {
        solarStationService.deleteById(stationId);
    }

    @PutMapping("/{id}")
    public final void update(@RequestBody final SolarStation station) throws Exception {
        solarStationService.update(station);
    }
}
