package ro.tuc.ds2020.dtos.builders;

import ro.tuc.ds2020.dtos.PersonDTO;
import ro.tuc.ds2020.dtos.PersonDetailsDTO;
import ro.tuc.ds2020.dtos.UserDTO;
import ro.tuc.ds2020.entities.Person;

public class PersonBuilder {

    private PersonBuilder() {
    }

    public static PersonDTO toPersonDTO(Person person) {
        return new PersonDTO(person.getId(), person.getName(), person.getAge(), person.getAddress(), person.getEmail());
    }

    public static PersonDetailsDTO toPersonDetailsDTO(Person person) {
        return new PersonDetailsDTO(person.getId(),
                person.getName(),
                person.getAddress(),
                person.getAge(),
                person.getEmail(),
                person.getPassword());
    }
    public static UserDTO personToUserDTO(Person person){
        return new UserDTO(person.getId(),
                person.getName(),
                person.getAddress(),
                person.getAge(),
                person.getEmail(),
                person.getRole(),
                person.getPassword());
    }

    public static Person toEntity(PersonDetailsDTO personDetailsDTO, String role) {
        return new Person(personDetailsDTO.getName(),
                personDetailsDTO.getAddress(),
                personDetailsDTO.getAge(),
                personDetailsDTO.getEmail(),
                personDetailsDTO.getPassword(),
                role);
    }
}
