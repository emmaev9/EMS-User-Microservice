package ro.tuc.ds2020.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ro.tuc.ds2020.dtos.PersonDTO;
import ro.tuc.ds2020.dtos.PersonDetailsDTO;
import ro.tuc.ds2020.entities.Person;
import ro.tuc.ds2020.services.PersonService;

@Configuration
public class ApplicationConfig {

    @Bean
    CommandLineRunner commandLineRunner2(PersonService personService){
        return args -> {

            PersonDetailsDTO admin1 = new PersonDetailsDTO(
                    "Popescu Vasile",
                    "Mihai Emnescu 100",
                    56,
                    "admin@yahoo.com",
                    "admin"

            );
            if(!personService.existsAdmin(admin1)) {
                personService.insert(admin1);
            }
            personService.updateRole(admin1);
        };
    }
}