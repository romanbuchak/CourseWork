package ua.lviv.iot.coursework.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ua.lviv.iot.coursework.models.SolarBattery;
import ua.lviv.iot.coursework.storage.SolarBatteryStorage;

import java.util.Collection;
import java.util.List;

public class SolarBatteryServiceImplTest {

    SolarBatteryStorage storage = new SolarBatteryStorage("src\\test\\resources\\battery", "solarBatteryTest-");
    SolarBatteryServiceImpl solarBatteryService = new SolarBatteryServiceImpl(storage);

    @Test
    void createSolarBatteryCollection() throws Exception {
        SolarBattery battery = new SolarBattery()
                .withId(1)
                .withModel("L150")
                .withCapacity(120.0)
                .withPrice(112.1);

        Collection<SolarBattery> created = solarBatteryService.create(List.of(battery));

        Assertions.assertEquals(created.size(), 1);
        Assertions.assertEquals(created.stream().findFirst().get(), battery);

    }

    @Test
    void getSolarBatteryById() throws Exception {
        SolarBattery battery = new SolarBattery()
                .withId(2)
                .withModel("L150")
                .withCapacity(120.0)
                .withPrice(112.1);

        solarBatteryService.create(List.of(battery));

        SolarBattery actual = solarBatteryService.getById(battery.getId());
        Assertions.assertNotNull(actual);
        org.assertj.core.api.Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(battery);

        //solarBatteryService.deleteById(battery.getId());
    }

    @Test
    void getAllBatteries() throws Exception {
        SolarBattery battery = new SolarBattery()
                .withId(1)
                .withModel("L150")
                .withCapacity(120.0)
                .withPrice(112.1);

        solarBatteryService.create(List.of(battery));

        Collection<SolarBattery> batteries = solarBatteryService.getAll();
        Assertions.assertNotNull(batteries);
        Assertions.assertFalse(batteries.isEmpty());

        //solarBatteryService.deleteById(battery.getId());
    }

    @Test
    void updateById() throws Exception {
        SolarBattery battery = new SolarBattery()
                .withId(1)
                .withModel("L150")
                .withCapacity(120.0)
                .withPrice(112.1);


        solarBatteryService.create(List.of(battery));

        Integer id = battery.getId();
        SolarBattery batteryFromCsv = solarBatteryService.getById(id);
        batteryFromCsv.setCapacity(55.0);
        solarBatteryService.update(batteryFromCsv);
        SolarBattery actual = solarBatteryService.getById(id);
        org.assertj.core.api.Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("capacity")
                .isEqualTo(batteryFromCsv);

        //solarBatteryService.deleteById(battery.getId());
    }
}

