package ua.lviv.iot.coursework.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ua.lviv.iot.coursework.models.SolarPanel;
import ua.lviv.iot.coursework.storage.SolarPanelStorage;
import java.util.Collection;
import java.util.List;

public class SolarPanelServiceImplTest {

    SolarPanelStorage storage = new SolarPanelStorage("src\\test\\resources\\panels", "solarPanelTest-");
    SolarPanelServiceImpl solarPanelService = new SolarPanelServiceImpl(storage);

    @Test
    void createSolarPanelCollection() throws Exception {
        SolarPanel panel = new SolarPanel()
                .withId(1)
                .withType("Carbonat")
                .withPower(150.0)
                .withTimeOfUsingPanels(1500L)
                .withPrice(250.2);

        Collection<SolarPanel> created = solarPanelService.create(List.of(panel));

        Assertions.assertEquals(created.size(), 1);
        Assertions.assertEquals(created.stream().findFirst().get(), panel);

    }

    @Test
    void getSolarPanelById() throws Exception {
        SolarPanel panel = new SolarPanel()
                .withId(1)
                .withType("Carbonat")
                .withPower(150.0)
                .withTimeOfUsingPanels(1500L)
                .withPrice(250.2);

        solarPanelService.create(List.of(panel));

        SolarPanel actual = solarPanelService.getById(panel.getId());
        Assertions.assertNotNull(actual);
        org.assertj.core.api.Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(panel);

        //solarPanelService.deleteById(panel.getId());
    }

    @Test
    void getAllPanels() throws Exception {
        SolarPanel panel = new SolarPanel()
                .withId(1)
                .withType("Carbonat")
                .withPower(150.0)
                .withTimeOfUsingPanels(1500L)
                .withPrice(250.2);

        solarPanelService.create(List.of(panel));

        Collection<SolarPanel> panels = solarPanelService.getAll();
        Assertions.assertNotNull(panels);
        Assertions.assertFalse(panels.isEmpty());

        //solarPanelService.deleteById(panel.getId());
    }

    @Test
    void updateById() throws Exception {
        SolarPanel panel = new SolarPanel()
                .withId(1)
                .withType("Carbonat")
                .withPower(150.0)
                .withTimeOfUsingPanels(1500L)
                .withPrice(250.2);


        solarPanelService.create(List.of(panel));

        Integer id = panel.getId();
        SolarPanel panelFromCsv = solarPanelService.getById(id);
        panelFromCsv.setPower(555.0);
        solarPanelService.update(panelFromCsv);
        SolarPanel actual = solarPanelService.getById(id);
        org.assertj.core.api.Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("power")
                .isEqualTo(panelFromCsv);

        //solarPanelService.deleteById(panel.getId());
    }
}