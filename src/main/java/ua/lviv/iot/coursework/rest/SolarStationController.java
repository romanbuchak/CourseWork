package ua.lviv.iot.coursework.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ua.lviv.iot.coursework.models.SolarStation;
import ua.lviv.iot.coursework.service.SolarStationService;
import ua.lviv.iot.coursework.service.SolarStationServiceImpl;

import java.io.FileNotFoundException;
import java.util.Collection;

@RestController
@RequestMapping("api/solar")
public class SolarStationController {

    private final SolarStationService solarStationService;
    private final SolarStationServiceImpl solarStationServiceImpl;

    @Autowired
    public SolarStationController(SolarStationService solarStationService, SolarStationServiceImpl solarStationServiceImpl) {
        this.solarStationService = solarStationService;
        this.solarStationServiceImpl = solarStationServiceImpl;
    }

    @PostMapping
    public final Collection<SolarStation> create(@RequestBody Collection<SolarStation> solars) throws Exception {
        return solarStationService.create(solars, null, false);
    }

    @GetMapping("/{id}")
    public final SolarStation getById(@PathVariable final Integer id) throws Exception {
//        SolarStation solarStation = solarStationService.getById(id);
//        if (solarStation == null)
//            throw new SolarStationNotFoundException("id:" + id);
//        return solarStation;
        return solarStationService.getById(id);

    }

    @GetMapping
    public final Collection<SolarStation> getAll() throws FileNotFoundException {
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
