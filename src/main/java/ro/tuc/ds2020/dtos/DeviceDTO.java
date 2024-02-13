package ro.tuc.ds2020.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.util.UUID;

@NoArgsConstructor
@Setter
@Getter
public class DeviceDTO extends RepresentationModel<DeviceDTO> {
    private UUID id;
    private String description;
    private String address;
    private int maxEnergyConsumption;

    public DeviceDTO(UUID id, String description,String address, int maxEnergyConsumption) {
        this.id = id;
        this.description = description;
        this.address = address;
        this.maxEnergyConsumption = maxEnergyConsumption;
    }
    public DeviceDTO(String description,String address, int maxEnergyConsumption) {
        this.description = description;
        this.address = address;
        this.maxEnergyConsumption = maxEnergyConsumption;
    }
}