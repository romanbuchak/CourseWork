package ua.lviv.iot.coursework.service;

import org.springframework.util.Assert;
import ua.lviv.iot.coursework.models.SolarStation;
import org.springframework.stereotype.Service;

import javax.management.OperationsException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SolarStationServiceImpl implements SolarStationService {

    @Override
    public final Collection<SolarStation> create(final Collection<SolarStation> solars,  String fileName, final boolean updateId) throws Exception {
        Assert.notNull(solars, "Solar stations cannot be null");

        if (!updateId) {
            setCorrectId(solars);
        }

        int firstEntityId = solars.stream().findFirst().get().getId();

        if (fileName == null) {
            fileName = "solarStation-" + LocalDate.now() + ".csv";
        }

        if (existFileByName(fileName)) {
            throw new Exception("Exist file with name" + fileName + " try update your file");
        }

        String writerResPath = String.format("%s%s%s", "C:\\Users\\Admin\\Desktop\\JavaIoT\\coursework\\src\\main\\resources\\templates", File.separator, fileName);

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

        file = getFileWithGivenId(stationId);

        Collection<SolarStation> stations = getRecordsFromFile(file.getAbsolutePath());

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
    public final SolarStation getById(final Integer id) throws Exception {

        Optional<SolarStation> solarStation;

        if (getAllRecords() != null) {

            solarStation = getAllRecords().stream().filter(station -> station.getId() == id).findFirst();
        } else {
            throw new Exception("Not found station with id:" + id);
        }

        return solarStation.get();
    }

    @Override
    public final Collection<SolarStation> getAll() throws FileNotFoundException {

        return getAllRecords();
    }

    @Override
    public final SolarStation deleteById(final Integer id) throws Exception {

        File fileToChange = getFileWithGivenId(id);
        Collection<SolarStation> solarStations = getRecordsFromFile(fileToChange.getAbsolutePath());
        String fileName = fileToChange.getName();
        SolarStation stationForDelete = getById(id);
        solarStations.remove(stationForDelete);
        fileToChange.delete();
        create(solarStations, fileName, true);

        return stationForDelete;
    }


    public final Integer getLastId() throws FileNotFoundException {
        OptionalInt biggestId;
        if (getAllRecords() != null) {
            biggestId = getAllRecords().stream().mapToInt(SolarStation::getId).max();
        } else {
            return 0;
        }
        return biggestId.getAsInt();
    }


    private Collection<SolarStation> getAllRecords() throws FileNotFoundException {

        List<File> files = getAllFiles();
        if (files == null) {
            return null;
        }
        Collection<Collection<SolarStation>> entities = new HashSet<>();

        for (File file : files) {
            entities.add(getRecordsFromFile(file.getAbsolutePath()));
        }

        if (entities.isEmpty()) {
            return null;
        }
        return entities.stream().flatMap(Collection::stream).collect(Collectors.toCollection(HashSet::new));
    }

    private Collection<SolarStation> getRecordsFromFile(String fileAbsolutePath) {
        List<List<String>> lines = new ArrayList<>();

        try (Scanner scanner = new Scanner((new File(fileAbsolutePath)))) {
            while (scanner.hasNext()) {
                lines.add(getRecordFromLine(scanner.nextLine()));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Collection<SolarStation> entities = new HashSet<>();

        for (List<String> line : lines) {
            if (!line.isEmpty() && !line.get(0).replaceAll("\\D", "").isEmpty()) {
                entities.add(fromSCVToEntity(line));
            }
        }
        return entities;
    }

    private List<String> getRecordFromLine(String line) {
        List<String> values = new ArrayList<>();
        try (Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter(", ");
            while (rowScanner.hasNext()) {
                values.add(rowScanner.next());
            }
        }
        return values;
    }

    private SolarStation fromSCVToEntity(List<String> fields) {
        SolarStation solarStation = new SolarStation();

        solarStation.setId(Integer.parseInt(fields.get(0)));
        solarStation.setType(fields.get(1));
        solarStation.setPower(Double.parseDouble(fields.get(2)));
        solarStation.setCapacity(Double.parseDouble(fields.get(3)));
        solarStation.setTimeOfUsingPanels(Long.parseLong(fields.get(4)));
        solarStation.setAddress(fields.get(5));
        solarStation.setProductionCapacity(Double.parseDouble(fields.get(6)));
        return solarStation;
    }

    private void setCorrectId(Collection<SolarStation> solarStations) throws FileNotFoundException {
        int lastId = getLastId();

        for (SolarStation station : solarStations) {
            lastId++;
            station.setId(lastId);
        }

    }

    private boolean existFileByName(String name) {

        List<File> existFiles = getAllFiles();

        if (existFiles != null) {
            for (File file : existFiles) {
                if (file.getName().equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<File> getAllFiles() {

        List<File> files = Arrays.asList(Objects.requireNonNull(new File("C:\\Users\\Admin\\Desktop\\JavaIoT\\coursework\\src\\main\\resources\\templates").listFiles()));
        if (files.isEmpty()) {
            return null;
        } else {
            return files;
        }
    }

    private File getFileWithGivenId(Integer id) throws Exception {
        List<File> allFiles = getAllFiles();
        Assert.notNull(allFiles, "There are no files with solar Station");
        if (!allFiles.isEmpty()) {
            for (File file : allFiles) {
                Collection<SolarStation> solarStations = getRecordsFromFile(file.getAbsolutePath());
                if (solarStations.stream().anyMatch(solarStation -> solarStation.getId().equals(id))) {
                    return file;
                }
            }
        }
        return null;
    }
}
