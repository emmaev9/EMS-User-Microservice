package ro.tuc.ds2020.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonDevice {
    private UUID id;
    private UUID user_id;
    private UUID device_id;
    private String address;
    private String email;

    public PersonDevice(UUID user_id, UUID device_id,String email, String address) {
        this.user_id = user_id;
        this.device_id = device_id;
        this.address = address;
        this.email = email;
    }
}
