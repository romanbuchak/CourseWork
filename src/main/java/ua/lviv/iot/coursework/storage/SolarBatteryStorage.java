package ua.lviv.iot.coursework.storage;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ua.lviv.iot.coursework.models.SolarBattery;

import javax.management.OperationsException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SolarBatteryStorage {

    private final Map<Integer, SolarBattery> batteries;
    private int lastId;

    private final String filesFolder;
    private final String fileNamePrefix;

    public SolarBatteryStorage() {
        this.filesFolder = "src\\main\\resources\\templates\\battery";
        this.fileNamePrefix = "solarBattery-";

        this.batteries = readAllBatteryOnInit();
        this.lastId = getLastId(batteries);
    }

    public SolarBatteryStorage(String filesFolder, String fileNamePrefix) {
        this.filesFolder = filesFolder;
        this.fileNamePrefix = fileNamePrefix;

        this.batteries = readAllBatteryOnInit();
        this.lastId = getLastId(batteries);
    }

    public SolarBatteryStorage(Map<Integer, SolarBattery> batteries, String filesFolder, String fileNamePrefix) {
        this.batteries = batteries;
        this.filesFolder = filesFolder;
        this.fileNamePrefix = fileNamePrefix;
    }

    private Map<Integer, SolarBattery> readAllBatteryOnInit() {
        return readAllRecordsByMonth(LocalDate.now())
                .stream()
                .collect(Collectors.toMap(
                        SolarBattery::getId,
                        Function.identity(),
                        (x, y) -> x
                ));
    }

    private int getLastId(Map<Integer, SolarBattery> batteries) {
        return batteries
                .values()
                .stream()
                .mapToInt(SolarBattery::getId)
                .max()
                .orElse(0);
    }

    public SolarBattery getById(int id) {
        return batteries.get(id);
    }

    public Collection<SolarBattery> create(Collection<SolarBattery> batteries) throws Exception {
        setCorrectId(batteries);

        String fileName = fileNamePrefix + LocalDate.now() + ".csv";

        if (existFileByName(fileName)) {
            appendToFile(batteries, fileName);
        } else {
            writeToFile(batteries, fileName);
        }

        for (SolarBattery battery : batteries) {
            this.batteries.put(battery.getId(), battery);
        }

        return batteries;
    }

    private void appendToFile(Collection<SolarBattery> batteries, String filename) throws Exception {
        String writerResPath = String.format("%s%s%s", filesFolder, File.separator, filename);

        try (FileWriter writer = new FileWriter(writerResPath, true)) {
            for (var battery : batteries) {
                writer.write(battery.toCSV());
                writer.write("\n");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }

    private void writeToFile(Collection<SolarBattery> batteries, String fileName) throws Exception {
        String writerResPath = String.format("%s%s%s", filesFolder, File.separator, fileName);

        try (FileWriter writer = new FileWriter(writerResPath)) {
            writer.write(SolarBattery.obtainHeaders());
            writer.write("\n");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw e;
        }

        appendToFile(batteries, fileName);
    }

    public SolarBattery update(SolarBattery actualSolarBattery) throws Exception {

        int batteryId = actualSolarBattery.getId();

        SolarBattery batteryFromFiles = batteries.get(batteryId);

        if (batteryFromFiles == null) {
            return null;
        }

        File file = getFileWithGivenId(batteryId);

        Collection<SolarBattery> batteriesFromFile =
                getRecordsFromFile(file.getAbsolutePath())
                        .stream()
                        .filter(battery -> battery.getId() != batteryId)
                        .collect(Collectors.toList());

        batteriesFromFile.add(actualSolarBattery);

        if (file.delete()) {
            writeToFile(batteriesFromFile, file.getName());
        } else {
            throw new OperationsException("Cannot delete file:" + file.getName());
        }

        batteries.remove(batteryId);
        batteries.put(batteryId, actualSolarBattery);

        return actualSolarBattery;
    }

    public boolean delete(int id) throws Exception {
        SolarBattery batteryForDelete = batteries.get(id);

        if (batteryForDelete == null) {
            return false;
        }

        File fileToChange = getFileWithGivenId(id);

        Collection<SolarBattery> solarBatteries = getRecordsFromFile(fileToChange.getAbsolutePath());
        String fileName = fileToChange.getName();

        solarBatteries.remove(batteryForDelete);
        fileToChange.delete();
        writeToFile(solarBatteries, fileName);
        batteries.remove(id);

        return true;
    }

    public Collection<SolarBattery> getAllRecords()  {
        return batteries.values();
    }

    private Set<SolarBattery> getRecordsFromFile(String fileAbsolutePath) {
        List<List<String>> lines = new ArrayList<>();

        try (Scanner scanner = new Scanner((new File(fileAbsolutePath)))) {
            while (scanner.hasNext()) {
                lines.add(getRecordFromLine(scanner.nextLine()));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Set<SolarBattery> entities = new HashSet<>();

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

    private SolarBattery fromSCVToEntity(List<String> fields) {
        SolarBattery solarBattery = new SolarBattery();

        solarBattery.setId(Integer.parseInt(fields.get(0)));
        solarBattery.setModel(fields.get(1));
        solarBattery.setCapacity(Double.parseDouble(fields.get(2)));
        solarBattery.setPrice(Double.parseDouble(fields.get(3)));
        return solarBattery;
    }

    private void setCorrectId(Collection<SolarBattery> solarBatteries) {
        int lastId = this.lastId;

        for (SolarBattery battery : solarBatteries) {
            lastId++;
            battery.setId(lastId);
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
        Assert.notNull(allFiles, "There are no files with solar battery");
        if (!allFiles.isEmpty()) {
            for (File file : allFiles) {
                Collection<SolarBattery> solarBatteries = getRecordsFromFile(file.getAbsolutePath());
                if (solarBatteries.stream().anyMatch(solarBattery -> solarBattery.getId().equals(id))) {
                    return file;
                }
            }
        }
        return null;
    }

    private Set<SolarBattery> readAllRecordsByMonth(LocalDate date) {
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

