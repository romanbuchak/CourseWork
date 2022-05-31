package service;

import models.SolarStation;

import java.util.Collection;

public interface SolarStationService {

    Collection<SolarStation> create(Collection<SolarStation> solar);
    Collection<SolarStation> update(Collection<SolarStation> solar);
    SolarStation getById(Integer id);
    Collection<SolarStation> getAll();
    SolarStation deleteById(Integer id);

}
