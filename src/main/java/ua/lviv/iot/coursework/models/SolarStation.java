package ua.lviv.iot.coursework.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class SolarStation {
    private Integer id;
    private String type;
    private Double power;
    private Double capacity;
    private Long timeOfUsingPanels;
    private String address;
    private Double productionCapacity;


    public String getHeaders() {
        return "ID, Type, Power, Capacity, TimeOfUsingPanels, Address, ProductCapacity";
    }
    public String toCSV(){
        return String.format("%s, %s, %s, %s, %s, %s, %s" , getId(), getType(), getPower(), getCapacity(), getTimeOfUsingPanels(),
                getAddress(), getProductionCapacity());
    }

}

