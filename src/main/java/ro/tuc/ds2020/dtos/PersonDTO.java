package ro.tuc.ds2020.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PersonDTO extends RepresentationModel<PersonDTO> {
    private UUID id;
    private String name;
    private int age;
    private String address;
    private String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonDTO personDTO = (PersonDTO) o;
        return age == personDTO.age &&
                Objects.equals(name, personDTO.name) &&
                Objects.equals(address, personDTO.address) &&
                Objects.equals(email, personDTO.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }
}
