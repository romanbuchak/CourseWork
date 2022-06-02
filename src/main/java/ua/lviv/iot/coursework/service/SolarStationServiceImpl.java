package ua.lviv.iot.coursework.service;

import ua.lviv.iot.coursework.models.SolarStation;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Service
public class SolarStationServiceImpl implements SolarStationService{

    @Override
    public Collection<SolarStation> create(Collection<SolarStation> solars) {
        String fileName = "solarStation-" + LocalDate.now() + ".csv";
            String writerResPath = String.format("%s%s%s%s%s", System.getProperty("user.dir"), File.separator, "\\src\\main\\resources\\templates", File.separator, fileName);
            try (FileWriter writer = new FileWriter(writerResPath)) {
                String lastClassName = "";
                for (var solar : solars) {
                    if (!solar.getClass().getSimpleName().equals(lastClassName)) {
                        if (solar.getId() == 1) {
                            writer.write(solar.getHeaders());
                        }
                        writer.write("\n");
                        lastClassName = solars.getClass().getSimpleName();
                    }
                    writer.write(solar.toCSV());
                    writer.write("\n");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        return solars;
    }

    @Override
    public Collection<SolarStation> update(Collection<SolarStation> solar) {
        return null;
    }

    @Override
    public SolarStation getById(Integer id) {
        return null;
    }

    @Override
    public Collection<SolarStation> getAll() {

        return null;
    }

    @Override
    public SolarStation deleteById(Integer id) {
        return null;
    }
    private Integer getLastId(){
        File directory = new File("C:\\Users\\Admin\\Desktop\\JavaIoT\\coursework\\src\\main\\resources\\templates");
        int count = directory.list().length;
        List<File> files = List.of(directory.listFiles());
        File file = files.get(count - 1);

        return null;
    }
}


