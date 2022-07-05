package ua.lviv.iot.coursework.service;

import ua.lviv.iot.coursework.models.SolarPanel;
import java.util.Collection;

public interface SolarPanelService {

    Collection<SolarPanel> create(Collection<SolarPanel> panel) throws Exception;

    SolarPanel update(SolarPanel panel) throws Exception;

    SolarPanel getById(Integer id);

    Collection<SolarPanel> getAll();

    SolarPanel deleteById(Integer id) throws Exception;

}

