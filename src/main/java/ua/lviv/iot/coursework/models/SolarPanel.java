package ua.lviv.iot.coursework.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@AllArgsConstructor
@NoArgsConstructor
@With

public class SolarPanel {
    private Integer id;
    private String type;
    private Double power;
    private Long timeOfUsingPanels;
    private Double price;


    public static String obtainHeaders() {
        return "ID, Type, Power, TimeOfUsingPanels, Price";
    }

    public final String toCSV() {
        return String.format("%s, %s, %s, %s, %s", getId(), getType(), getPower(), getTimeOfUsingPanels(),
                getPrice());
    }
}
