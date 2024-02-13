package ro.tuc.ds2020.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import ro.tuc.ds2020.dtos.PersonDTO;
import ro.tuc.ds2020.dtos.PersonDetailsDTO;
import ro.tuc.ds2020.dtos.UpdatePersonDTO;
import ro.tuc.ds2020.entities.Person;
import ro.tuc.ds2020.services.PersonService;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/person")
public class PersonController {

    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;

    }

    @GetMapping("/personHome")
    public String personHomePage(){
        return "personHome";
    }

    @GetMapping("/getAllClients")
    public ResponseEntity<List<PersonDTO>> getPersons() {
        List<PersonDTO> dtos = personService.findPersons();
        for (PersonDTO dto : dtos) {
            Link personLink = linkTo(methodOn(PersonController.class)
                    .getPerson(dto.getId())).withRel("personDetails");
            dto.add(personLink);
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<UUID> insertPerson(@Valid @RequestBody PersonDetailsDTO personDTO) {
        UUID personID = personService.insert(personDTO);
        return new ResponseEntity<>(personID, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<PersonDetailsDTO> getPerson(@PathVariable("id") UUID personId) {
        PersonDetailsDTO dto = personService.findPersonById(personId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/getClientById")
    public ResponseEntity<PersonDetailsDTO>getClient(@RequestParam(value="id") UUID id){
        PersonDetailsDTO dto = personService.findPersonById(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Transactional
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deletePerson(@PathVariable("id") UUID personId){
        personService.delete(personId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Transactional
    @PutMapping()
    public ResponseEntity<?> updatePerson(@RequestBody PersonDetailsDTO updatePersonDTO){
        System.out.println("Id of the client fr update: " +updatePersonDTO.getId());
        Person dto = personService.update(updatePersonDTO);
        return new ResponseEntity<>(dto, HttpStatus.OK);

    }
}
