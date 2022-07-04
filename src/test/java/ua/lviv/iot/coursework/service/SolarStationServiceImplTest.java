package ua.lviv.iot.coursework.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ua.lviv.iot.coursework.models.SolarStation;
import java.util.Collection;
import java.util.List;

public class SolarStationServiceImplTest {

    SolarStationServiceStorage storage = new SolarStationServiceStorage("src\\test\\resources", "solarStationTest-");
    SolarStationServiceImpl solarStationService = new SolarStationServiceImpl(storage);

    @Test
    void createSolarStationCollection() throws Exception {
        SolarStation station = new SolarStation()
                .withAddress("Bandery 1")
                .withCapacity(150.0)
                .withId(1)
                .withPower(1500.0)
                .withProductionCapacity(123.0)
                .withTimeOfUsingPanels(15200L)
                .withType("Static");

        Collection<SolarStation> created = solarStationService.create(List.of(station));

        Assertions.assertEquals(created.size(), 1);
        Assertions.assertEquals(created.stream().findFirst().get(), station);

    }

    @Test
    void getSolarStationById() throws Exception {
        SolarStation station = new SolarStation()
                .withAddress("Bandery 1")
                .withCapacity(150.0)
                .withId(1)
                .withPower(500.0)
                .withProductionCapacity(123.0)
                .withTimeOfUsingPanels(15200L)
                .withType("Static");

        solarStationService.create(List.of(station));

        SolarStation actual = solarStationService.getById(station.getId());
        Assertions.assertNotNull(actual);
        org.assertj.core.api.Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(station);

        //solarStationService.deleteById(station.getId());
    }

    @Test
    void getAllSolars() throws Exception {
        SolarStation station = new SolarStation()
                .withAddress("Bandery 1")
                .withCapacity(150.0)
                .withId(1)
                .withPower(100.0)
                .withProductionCapacity(123.0)
                .withTimeOfUsingPanels(15200L)
                .withType("Static");

        solarStationService.create(List.of(station));

        Collection<SolarStation> solars = solarStationService.getAll();
        Assertions.assertNotNull(solars);
        Assertions.assertFalse(solars.isEmpty());

        //solarStationService.deleteById(station.getId());
    }

    @Test
    void updateById() throws Exception {
        SolarStation station = new SolarStation()
                .withAddress("Bandery 1")
                .withCapacity(150.0)
                .withId(1)
                .withPower(150.0)
                .withProductionCapacity(123.0)
                .withTimeOfUsingPanels(15200L)
                .withType("Static");

        solarStationService.create(List.of(station));

        Integer id = station.getId();
        SolarStation stationFromCsv = solarStationService.getById(id);
        stationFromCsv.setPower(555.0);
        solarStationService.update(stationFromCsv);
        SolarStation actual = solarStationService.getById(id);
        org.assertj.core.api.Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("power")
                .isEqualTo(stationFromCsv);

        //solarStationService.deleteById(station.getId());
    }
}
