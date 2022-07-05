package ua.lviv.iot.coursework.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@AllArgsConstructor
@NoArgsConstructor
@With

public class SolarBattery {
    private Integer id;
    private String model;
    private Double capacity;
    private Double price;


    public static String obtainHeaders() {
        return "ID, Model, Capacity, Price";
    }

    public final String toCSV() {
        return String.format("%s, %s, %s, %s", getId(), getModel(), getCapacity(), getPrice());
    }
}
