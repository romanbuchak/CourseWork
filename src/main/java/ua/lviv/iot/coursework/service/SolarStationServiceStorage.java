package ua.lviv.iot.coursework.service;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ua.lviv.iot.coursework.models.SolarStation;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SolarStationServiceStorage {
    public final Integer getLastId() {
        OptionalInt biggestId;
        if (getAllRecords() != null) {
            biggestId = getAllRecords().stream().mapToInt(SolarStation::getId).max();
        } else {
            return 0;
        }
        return biggestId.getAsInt();
    }

    protected Collection<SolarStation> getAllRecords()  {

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

    protected Collection<SolarStation> getRecordsFromFile(String fileAbsolutePath) {
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

    protected List<String> getRecordFromLine(String line) {
        List<String> values = new ArrayList<>();
        try (Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter(", ");
            while (rowScanner.hasNext()) {
                values.add(rowScanner.next());
            }
        }
        return values;
    }

    protected SolarStation fromSCVToEntity(List<String> fields) {
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

    protected void setCorrectId(Collection<SolarStation> solarStations) {
        int lastId = getLastId();

        for (SolarStation station : solarStations) {
            lastId++;
            station.setId(lastId);
        }
    }

    protected boolean existFileByName(String name) {

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
        List<File> files = Arrays.asList(Objects.requireNonNull(new File("src\\main\\resources\\templates").listFiles()));
        if (files.isEmpty()) {
            return null;
        } else {
            return files;
        }
    }

    public File getFileWithGivenId(Integer id) {
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
