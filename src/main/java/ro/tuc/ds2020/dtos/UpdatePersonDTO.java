package ro.tuc.ds2020.dtos;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePersonDTO extends RepresentationModel<UpdatePersonDTO> {
    private UUID id;
    private String name;
    private String address;
    private int age;
    private String email;
}
