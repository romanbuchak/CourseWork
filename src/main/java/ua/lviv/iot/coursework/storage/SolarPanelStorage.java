package ua.lviv.iot.coursework.storage;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ua.lviv.iot.coursework.models.SolarPanel;
import javax.management.OperationsException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SolarPanelStorage {

    private final Map<Integer, SolarPanel> panels;
    private int lastId;

    private final String filesFolder;
    private final String fileNamePrefix;

    public SolarPanelStorage() {
        this.filesFolder = "src\\main\\resources\\templates\\panels";
        this.fileNamePrefix = "solarPanel-";

        this.panels = readAllPanelOnInit();
        this.lastId = getLastId(panels);
    }

    public SolarPanelStorage(String filesFolder, String fileNamePrefix) {
        this.filesFolder = filesFolder;
        this.fileNamePrefix = fileNamePrefix;

        this.panels = readAllPanelOnInit();
        this.lastId = getLastId(panels);
    }

    public SolarPanelStorage(Map<Integer, SolarPanel> panels, String filesFolder, String fileNamePrefix) {
        this.panels = panels;
        this.filesFolder = filesFolder;
        this.fileNamePrefix = fileNamePrefix;
    }

    private Map<Integer, SolarPanel> readAllPanelOnInit() {
        return readAllRecordsByMonth(LocalDate.now())
                .stream()
                .collect(Collectors.toMap(
                        SolarPanel::getId,
                        Function.identity(),
                        (x, y) -> x
                ));
    }

    private int getLastId(Map<Integer, SolarPanel> panels) {
        return panels
                .values()
                .stream()
                .mapToInt(SolarPanel::getId)
                .max()
                .orElse(0);
    }

    public SolarPanel getById(int id) {
        return panels.get(id);
    }

    public Collection<SolarPanel> create(Collection<SolarPanel> panels) throws Exception {
        setCorrectId(panels);

        String fileName = fileNamePrefix + LocalDate.now() + ".csv";

        if (existFileByName(fileName)) {
            appendToFile(panels, fileName);
        } else {
            writeToFile(panels, fileName);
        }

        for (SolarPanel panel : panels) {
            this.panels.put(panel.getId(), panel);
        }

        return panels;
    }

    private void appendToFile(Collection<SolarPanel> panels, String filename) throws Exception {
        String writerResPath = String.format("%s%s%s", filesFolder, File.separator, filename);

        try (FileWriter writer = new FileWriter(writerResPath, true)) {
            for (var panel : panels) {
                writer.write(panel.toCSV());
                writer.write("\n");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }

    private void writeToFile(Collection<SolarPanel> panels, String fileName) throws Exception {
        String writerResPath = String.format("%s%s%s", filesFolder, File.separator, fileName);

        try (FileWriter writer = new FileWriter(writerResPath)) {
            writer.write(SolarPanel.obtainHeaders());
            writer.write("\n");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw e;
        }

        appendToFile(panels, fileName);
    }

    public SolarPanel update(SolarPanel actualSolarPanel) throws Exception {

        int panelId = actualSolarPanel.getId();

        SolarPanel panelFromFiles = panels.get(panelId);

        if (panelFromFiles == null) {
            return null;
        }

        File file = getFileWithGivenId(panelId);

        Collection<SolarPanel> panelsFromFile =
                getRecordsFromFile(file.getAbsolutePath())
                        .stream()
                        .filter(panel -> panel.getId() != panelId)
                        .collect(Collectors.toList());

        panelsFromFile.add(actualSolarPanel);

        if (file.delete()) {
            writeToFile(panelsFromFile, file.getName());
        } else {
            throw new OperationsException("Cannot delete file:" + file.getName());
        }

        panels.remove(panelId);
        panels.put(panelId, actualSolarPanel);

        return actualSolarPanel;
    }

    public boolean delete(int id) throws Exception {
        SolarPanel panelForDelete = panels.get(id);

        if (panelForDelete == null) {
            return false;
        }

        File fileToChange = getFileWithGivenId(id);

        Collection<SolarPanel> solarPanels = getRecordsFromFile(fileToChange.getAbsolutePath());
        String fileName = fileToChange.getName();

        solarPanels.remove(panelForDelete);
        fileToChange.delete();
        writeToFile(solarPanels, fileName);
        panels.remove(id);

        return true;
    }

    public Collection<SolarPanel> getAllRecords()  {
        return panels.values();
    }

    private Set<SolarPanel> getRecordsFromFile(String fileAbsolutePath) {
        List<List<String>> lines = new ArrayList<>();

        try (Scanner scanner = new Scanner((new File(fileAbsolutePath)))) {
            while (scanner.hasNext()) {
                lines.add(getRecordFromLine(scanner.nextLine()));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Set<SolarPanel> entities = new HashSet<>();

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

    private SolarPanel fromSCVToEntity(List<String> fields) {
        SolarPanel solarPanel = new SolarPanel();

        solarPanel.setId(Integer.parseInt(fields.get(0)));
        solarPanel.setType(fields.get(1));
        solarPanel.setPower(Double.parseDouble(fields.get(2)));
        solarPanel.setTimeOfUsingPanels(Long.parseLong(fields.get(3)));
        solarPanel.setPrice(Double.parseDouble(fields.get(4)));
        return solarPanel;
    }

    private void setCorrectId(Collection<SolarPanel> solarPanels) {
        int lastId = this.lastId;

        for (SolarPanel panel : solarPanels) {
            lastId++;
            panel.setId(lastId);
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
        Assert.notNull(allFiles, "There are no files with solar Panel");
        if (!allFiles.isEmpty()) {
            for (File file : allFiles) {
                Collection<SolarPanel> solarPanels = getRecordsFromFile(file.getAbsolutePath());
                if (solarPanels.stream().anyMatch(solarPanel -> solarPanel.getId().equals(id))) {
                    return file;
                }
            }
        }
        return null;
    }

    private Set<SolarPanel> readAllRecordsByMonth(LocalDate date) {
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
