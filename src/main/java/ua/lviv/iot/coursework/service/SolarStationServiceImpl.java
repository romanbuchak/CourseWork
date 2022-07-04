package ua.lviv.iot.coursework.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ua.lviv.iot.coursework.models.SolarStation;
import ua.lviv.iot.coursework.storage.SolarStationStorage;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.Optional;


@Service
public class SolarStationServiceImpl implements SolarStationService {
    private final SolarStationStorage storage;

@Autowired
    public SolarStationServiceImpl(SolarStationStorage storage) {
        this.storage = storage;
    }

    @Override
    public final Collection<SolarStation> create(final Collection<SolarStation> solars) throws Exception {
        Assert.notNull(solars, "Solar stations cannot be null");

        return storage.create(solars);
    }

    @Override
    public final SolarStation update(SolarStation actualSolarStation) throws Exception {
        Assert.notNull(actualSolarStation, "Solar station cannot be null");
        Assert.notNull(actualSolarStation.getId(), "Solar station ID cannot be null");

        return storage.update(actualSolarStation);
    }

    @Override
    public final SolarStation getById(final Integer id) {
        Optional<SolarStation> solarStation = Optional.ofNullable(storage.getById(id));

        if (solarStation.isEmpty())
            throw new EntityNotFoundException("Not found station with id:" + id);

        return solarStation.get();

    }

    @Override
    public final Collection<SolarStation> getAll() {
        return storage.getAllRecords();
    }

    @Override
    public final SolarStation deleteById(final Integer id) throws Exception {
        boolean result = storage.delete(id);

        if (!result) {
            throw new Exception("Station with id" + id + " not found");
        }
        return null;
    }
}
