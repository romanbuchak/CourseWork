package ua.lviv.iot.coursework.storage;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ua.lviv.iot.coursework.models.SolarStation;
import javax.management.OperationsException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SolarStationStorage {

    private final Map<Integer, SolarStation> stations;
    private int lastId;

    private final String filesFolder;
    private final String fileNamePrefix;

    public SolarStationStorage() {
        this.filesFolder = "src\\main\\resources\\templates";
        this.fileNamePrefix = "solarStation-";

        this.stations = readAllStationsOnInit();
        this.lastId = getLastId(stations);
    }

    public SolarStationStorage(String filesFolder, String fileNamePrefix) {
        this.filesFolder = filesFolder;
        this.fileNamePrefix = fileNamePrefix;

        this.stations = readAllStationsOnInit();
        this.lastId = getLastId(stations);
    }

    public SolarStationStorage(Map<Integer, SolarStation> stations, String filesFolder, String fileNamePrefix) {
        this.stations = stations;
        this.filesFolder = filesFolder;
        this.fileNamePrefix = fileNamePrefix;
    }

    private Map<Integer, SolarStation> readAllStationsOnInit() {
        return readAllRecordsByMonth(LocalDate.now())
                .stream()
                .collect(Collectors.toMap(
                        SolarStation::getId,
                        Function.identity(),
                        (x, y) -> x
                ));
    }

    private int getLastId(Map<Integer, SolarStation> stations) {
        return stations
                .values()
                .stream()
                .mapToInt(SolarStation::getId)
                .max()
                .orElse(0);
    }

    public SolarStation getById(int id) {
        return stations.get(id);
    }

    public Collection<SolarStation> create(Collection<SolarStation> solars) throws Exception {
        setCorrectId(solars);

        String fileName = fileNamePrefix + LocalDate.now() + ".csv";

        if (existFileByName(fileName)) {
            appendToFile(solars, fileName);
        } else {
            writeToFile(solars, fileName);
        }

        for (SolarStation solar : solars) {
            stations.put(solar.getId(), solar);
        }

        return solars;
    }

    private void appendToFile(Collection<SolarStation> solars, String filename) throws Exception {
        String writerResPath = String.format("%s%s%s", filesFolder, File.separator, filename);

        try (FileWriter writer = new FileWriter(writerResPath, true)) {
            for (var solar : solars) {
                writer.write(solar.toCSV());
                writer.write("\n");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }

    private void writeToFile(Collection<SolarStation> solars, String fileName) throws Exception {
        String writerResPath = String.format("%s%s%s", filesFolder, File.separator, fileName);

        try (FileWriter writer = new FileWriter(writerResPath)) {
            writer.write(SolarStation.obtainHeaders());
            writer.write("\n");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw e;
        }

        appendToFile(solars, fileName);
    }

    public SolarStation update(SolarStation actualSolarStation) throws Exception {

        int stationId = actualSolarStation.getId();

        SolarStation stationFromFiles = stations.get(stationId);

        if (stationFromFiles == null) {
            return null;
        }

        File file = getFileWithGivenId(stationId);

        Collection<SolarStation> stationsFromFile =
                getRecordsFromFile(file.getAbsolutePath())
                        .stream()
                        .filter(station -> station.getId() != stationId)
                        .collect(Collectors.toList());

        stationsFromFile.add(actualSolarStation);

        if (file.delete()) {
            writeToFile(stationsFromFile, file.getName());
        } else {
            throw new OperationsException("Cannot delete file:" + file.getName());
        }

        stations.remove(stationId);
        stations.put(stationId, actualSolarStation);

        return actualSolarStation;
    }

    public boolean delete(int id) throws Exception {
        SolarStation stationForDelete = stations.get(id);

        if (stationForDelete == null) {
            return false;
        }

        File fileToChange = getFileWithGivenId(id);

        Collection<SolarStation> solarStations = getRecordsFromFile(fileToChange.getAbsolutePath());
        String fileName = fileToChange.getName();

        solarStations.remove(stationForDelete);
        fileToChange.delete();
        writeToFile(solarStations, fileName);
        stations.remove(id);

        return true;
    }

    public Collection<SolarStation> getAllRecords()  {
        return stations.values();
    }

    private Set<SolarStation> getRecordsFromFile(String fileAbsolutePath) {
        List<List<String>> lines = new ArrayList<>();

        try (Scanner scanner = new Scanner((new File(fileAbsolutePath)))) {
            while (scanner.hasNext()) {
                lines.add(getRecordFromLine(scanner.nextLine()));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Set<SolarStation> entities = new HashSet<>();

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

    private void setCorrectId(Collection<SolarStation> solarStations) {
        int lastId = this.lastId;

        for (SolarStation station : solarStations) {
            lastId++;
            station.setId(lastId);
        }

        this.lastId = lastId;
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
        List<File> files = Arrays.asList(Objects.requireNonNull(new File(filesFolder).listFiles()));
        if (files.isEmpty()) {
            return null;
        } else {
            return files;
        }
    }

    private File getFileWithGivenId(Integer id) {
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

    private Set<SolarStation> readAllRecordsByMonth(LocalDate date) {
        List<File> files = getAllFiles();

        if (files == null) {
            return Set.of();
        } else {
            return files
                    .stream()
                    .filter(file -> isFileOfMonth(file.getName(), date))
                    .flatMap(file -> getRecordsFromFile(file.getAbsolutePath()).stream())
                    .collect(Collectors.toSet());
        }
    }

    private boolean isFileOfMonth(String filename, LocalDate date) {
        String fileDate = getDateFromFileName(filename);

        return LocalDate.parse(fileDate)
                .plusDays(1)
                .isAfter(date.withDayOfMonth(1));
    }

    private String getDateFromFileName(String filename) {
        return filename.split("\\.")[0].replace(fileNamePrefix, "");
    }
}
