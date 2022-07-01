package ua.lviv.iot.coursework.models;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@With
@Getter
public class SolarStation {
    private Integer id;
    private String type;
    private Double power;
    private Double capacity;
    private Long timeOfUsingPanels;
    private String address;
    private Double productionCapacity;


    public static String obtainHeaders() {
        return "ID, Type, Power, Capacity, TimeOfUsingPanels, Address, ProductCapacity";
    }

    public final String toCSV() {
        return String.format("%s, %s, %s, %s, %s, %s, %s", getId(), getType(), getPower(), getCapacity(), getTimeOfUsingPanels(),
                getAddress(), getProductionCapacity());
    }

}
