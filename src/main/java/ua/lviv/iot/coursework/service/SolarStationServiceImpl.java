package ua.lviv.iot.coursework.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ua.lviv.iot.coursework.models.SolarStation;

import javax.management.OperationsException;
import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.*;

@Service
public class SolarStationServiceImpl implements SolarStationService {
    private final SolarStationServiceStorage storage;
@Autowired
    public SolarStationServiceImpl(SolarStationServiceStorage storage) {
        this.storage = storage;
    }

    @Override
    public final Collection<SolarStation> create(final Collection<SolarStation> solars, String fileName, final boolean updateId) throws Exception {
        Assert.notNull(solars, "Solar stations cannot be null");

        if (!updateId) {
            storage.setCorrectId(solars);
        }

        int firstEntityId = solars.stream().findFirst().get().getId();

        if (fileName == null) {
            fileName = "solarStation-" + LocalDate.now() + ".csv";
        }

        if (storage.existFileByName(fileName)) {
            throw new Exception("Exist file with name " + fileName + " try update your file");
        }

        String writerResPath = String.format("%s%s%s", "src\\main\\resources\\templates", File.separator, fileName);

        try (FileWriter writer = new FileWriter(writerResPath)) {
            String lastClassName = "";
            for (var solar : solars) {
                if (!solar.getClass().getSimpleName().equals(lastClassName)) {
                    if (solar.getId() == firstEntityId) {
                        writer.write(solar.obtainHeaders());
                    }

                    writer.write("\n");
                    lastClassName = solars.getClass().getSimpleName();
                }
                writer.write(solar.toCSV());
                writer.write("\n");
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return solars;
    }

    @Override
    public final SolarStation update(SolarStation actualSolarStation) throws Exception {
        Assert.notNull(actualSolarStation, "Solar station cannot be null");
        Assert.notNull(actualSolarStation.getId(), "Solar station ID cannot be null");

        int stationId = actualSolarStation.getId();

        File file;
        SolarStation stationFromFiles;

        file = storage.getFileWithGivenId(stationId);

        Collection<SolarStation> stations = storage.getRecordsFromFile(file.getAbsolutePath());

        stationFromFiles = getById(stationId);

        stations.remove(stationFromFiles);

        stations.add(actualSolarStation);

        if (file.delete()) {
            create(stations, file.getName(), true);
        } else {
            throw new OperationsException("Cannot delete file:" + file.getName());
        }

        return actualSolarStation;
    }

    @Override
    public final SolarStation getById(final Integer id) {

        Optional<SolarStation> solarStation;

        solarStation = storage.getAllRecords().stream().filter(station -> station.getId().equals(id)).findFirst();

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

        File fileToChange = storage.getFileWithGivenId(id);
        Collection<SolarStation> solarStations = storage.getRecordsFromFile(fileToChange.getAbsolutePath());
        String fileName = fileToChange.getName();
        SolarStation stationForDelete = getById(id);
        solarStations.remove(stationForDelete);
        fileToChange.delete();
        create(solarStations, fileName, true);

        return stationForDelete;
    }
}
