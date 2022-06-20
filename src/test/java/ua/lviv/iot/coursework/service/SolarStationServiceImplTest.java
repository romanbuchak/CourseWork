package ua.lviv.iot.coursework.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.lviv.iot.coursework.models.SolarStation;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashSet;


@ExtendWith(MockitoExtension.class)

public class SolarStationServiceImplTest {

    @InjectMocks
    SolarStationServiceImpl solarStationService;
    private SolarStation station = new SolarStation();
    private final Collection<SolarStation> solarStations = new HashSet<>();
    Collection<SolarStation> actual;
    private static Integer fileIdentifier = 0;

    @BeforeEach
    void beforeEach() throws Exception {
        fileIdentifier++;
        station = station.withAddress("Bandery 1")
                .withCapacity(150.0)
                .withId(1)
                .withPower(1500.0)
                .withProductionCapacity(123.0)
                .withTimeOfUsingPanels(15200L)
                .withType("Static");

        solarStations.add(station);
        String testFileName = "test-Solar-file%s.csv";
        actual = solarStationService.create(solarStations, String.format(testFileName, fileIdentifier), false);
    }

    @AfterEach
    void afterEach() {
        solarStations.clear();
    }

    @Test
    void createSolarStationCollection() {
        Assertions.assertEquals(solarStations.size(), actual.size());
    }

    @Test
    void getSolarStationById() throws Exception {
        SolarStation actual = solarStationService.getById(station.getId());
        Assertions.assertNotNull(actual);
        org.assertj.core.api.Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(station);
    }

    @Test
    void getAllSolars() throws FileNotFoundException {
        Collection<SolarStation> solars = solarStationService.getAll();
        Assertions.assertNotNull(solars);
        Assertions.assertFalse(solars.isEmpty());
    }

    @Test
    void updateById() throws Exception {
        Integer lastId = solarStationService.getLastId();
        SolarStation stationFromCsv = solarStationService.getById(lastId);
        stationFromCsv.setPower(555.0);
        solarStationService.update(stationFromCsv);
        SolarStation actual = solarStationService.getById(lastId);
        org.assertj.core.api.Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("power")
                .isEqualTo(stationFromCsv);
    }
}
