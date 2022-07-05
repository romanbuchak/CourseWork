package ua.lviv.iot.coursework.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ua.lviv.iot.coursework.models.SolarBattery;
import ua.lviv.iot.coursework.storage.SolarBatteryStorage;
import javax.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.Optional;

@Service
public class SolarBatteryServiceImpl implements SolarBatteryService {
    private final SolarBatteryStorage storage;

    @Autowired
    public SolarBatteryServiceImpl(SolarBatteryStorage storage) {
        this.storage = storage;
    }

    @Override
    public final Collection<SolarBattery> create(final Collection<SolarBattery> batteries) throws Exception {
        Assert.notNull(batteries, "Solar batteries cannot be null");

        return storage.create(batteries);
    }

    @Override
    public final SolarBattery update(SolarBattery actualSolarBattery) throws Exception {
        Assert.notNull(actualSolarBattery, "Solar battery cannot be null");
        Assert.notNull(actualSolarBattery.getId(), "Solar battery ID cannot be null");

        return storage.update(actualSolarBattery);
    }

    @Override
    public final SolarBattery getById(final Integer id) {
        Optional<SolarBattery> solarBattery = Optional.ofNullable(storage.getById(id));

        if (solarBattery.isEmpty())
            throw new EntityNotFoundException("Not found battery with id:" + id);

        return solarBattery.get();

    }

    @Override
    public final Collection<SolarBattery> getAll() {
        return storage.getAllRecords();
    }

    @Override
    public final SolarBattery deleteById(final Integer id) throws Exception {
        boolean result = storage.delete(id);

        if (!result) {
            throw new Exception("Battery with id" + id + " not found");
        }
        return null;
    }
}
