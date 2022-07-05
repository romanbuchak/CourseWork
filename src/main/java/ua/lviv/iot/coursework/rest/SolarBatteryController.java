package ua.lviv.iot.coursework.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ua.lviv.iot.coursework.models.SolarBattery;
import ua.lviv.iot.coursework.service.SolarBatteryService;

import java.util.Collection;


@RestController
@RequestMapping("api/battery")
public class SolarBatteryController {
    private final SolarBatteryService solarBatteryService;

    @Autowired
    public SolarBatteryController(SolarBatteryService solarBatteryService) {
        this.solarBatteryService = solarBatteryService;
    }

    @PostMapping
    public final Collection<SolarBattery> create(@RequestBody Collection<SolarBattery> batteries) throws Exception {
        return solarBatteryService.create(batteries);
    }

    @GetMapping("/{id}")
    public final SolarBattery getById(@PathVariable final Integer id) {
        return solarBatteryService.getById(id);
    }

    @GetMapping
    public final Collection<SolarBattery> getAll() {
        return solarBatteryService.getAll();
    }

    @DeleteMapping("/{batteryId}")
    public final void deleteById(@PathVariable final Integer batteryId) throws Exception {
        solarBatteryService.deleteById(batteryId);
    }

    @PutMapping("/{id}")
    public final void update(@RequestBody final SolarBattery battery) throws Exception {
        solarBatteryService.update(battery);
    }
}
