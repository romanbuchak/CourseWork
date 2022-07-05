package ua.lviv.iot.coursework.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ua.lviv.iot.coursework.models.SolarPanel;
import ua.lviv.iot.coursework.storage.SolarPanelStorage;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.Optional;

@Service
public class SolarPanelServiceImpl implements SolarPanelService {
    private final SolarPanelStorage storage;

    @Autowired
    public SolarPanelServiceImpl(SolarPanelStorage storage) {
        this.storage = storage;
    }

    @Override
    public final Collection<SolarPanel> create(final Collection<SolarPanel> panels) throws Exception {
        Assert.notNull(panels, "Solar panels cannot be null");

        return storage.create(panels);
    }

    @Override
    public final SolarPanel update(SolarPanel actualSolarPanel) throws Exception {
        Assert.notNull(actualSolarPanel, "Solar panel cannot be null");
        Assert.notNull(actualSolarPanel.getId(), "Solar panel ID cannot be null");

        return storage.update(actualSolarPanel);
    }

    @Override
    public final SolarPanel getById(final Integer id) {
        Optional<SolarPanel> solarPanel = Optional.ofNullable(storage.getById(id));

        if (solarPanel.isEmpty())
            throw new EntityNotFoundException("Not found panel with id:" + id);

        return solarPanel.get();

    }

    @Override
    public final Collection<SolarPanel> getAll() {
        return storage.getAllRecords();
    }

    @Override
    public final SolarPanel deleteById(final Integer id) throws Exception {
        boolean result = storage.delete(id);

        if (!result) {
            throw new Exception("Panel with id" + id + " not found");
        }
        return null;
    }
}
