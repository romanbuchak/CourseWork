package ua.lviv.iot.coursework.service;

import ua.lviv.iot.coursework.models.SolarStation;

import java.io.FileNotFoundException;
import java.util.Collection;

public interface SolarStationService {

    Collection<SolarStation> create(Collection<SolarStation> solar, String fileName, boolean changeId) throws Exception;

    SolarStation update(SolarStation solar) throws Exception;

    SolarStation getById(Integer id) throws Exception;

    Collection<SolarStation> getAll() throws FileNotFoundException;

    SolarStation deleteById(Integer id) throws Exception;
}
