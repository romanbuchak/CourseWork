package ua.lviv.iot.coursework.service;

import ua.lviv.iot.coursework.models.SolarBattery;

import java.util.Collection;

public interface SolarBatteryService {

    Collection<SolarBattery> create(Collection<SolarBattery> battery) throws Exception;

    SolarBattery update(SolarBattery battery) throws Exception;

    SolarBattery getById(Integer id);

    Collection<SolarBattery> getAll();

    SolarBattery deleteById(Integer id) throws Exception;

}
