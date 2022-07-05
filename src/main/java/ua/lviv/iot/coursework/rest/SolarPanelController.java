package ua.lviv.iot.coursework.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ua.lviv.iot.coursework.models.SolarPanel;
import ua.lviv.iot.coursework.service.SolarPanelService;

import java.util.Collection;

@RestController
@RequestMapping("api/panel")
public class SolarPanelController {
    private final SolarPanelService solarPanelService;

    @Autowired
    public SolarPanelController(SolarPanelService solarPanelService) {
        this.solarPanelService = solarPanelService;
    }

    @PostMapping
    public final Collection<SolarPanel> create(@RequestBody Collection<SolarPanel> panels) throws Exception {
        return solarPanelService.create(panels);
    }

    @GetMapping("/{id}")
    public final SolarPanel getById(@PathVariable final Integer id) {
        return solarPanelService.getById(id);
    }

    @GetMapping
    public final Collection<SolarPanel> getAll() {
        return solarPanelService.getAll();
    }

    @DeleteMapping("/{panelId}")
    public final void deleteById(@PathVariable final Integer panelId) throws Exception {
        solarPanelService.deleteById(panelId);
    }

    @PutMapping("/{id}")
    public final void update(@RequestBody final SolarPanel panel) throws Exception {
        solarPanelService.update(panel);
    }
}
