package ro.tuc.ds2020.services;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.ResourceNotFoundException;
import ro.tuc.ds2020.dtos.IdDTO;
import ro.tuc.ds2020.dtos.PersonDTO;
import ro.tuc.ds2020.dtos.PersonDetailsDTO;
import ro.tuc.ds2020.dtos.UserDTO;
import ro.tuc.ds2020.dtos.builders.PersonBuilder;
import ro.tuc.ds2020.entities.Person;
import ro.tuc.ds2020.entities.PersonDevice;
import ro.tuc.ds2020.repositories.PersonRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PersonService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonService.class);
    private final PersonRepository personRepository;
    private final PasswordEncoder encoder;
    private final WebClient.Builder webClientBuilder;

    @Value("userExchange")
    private String exchange;

    @Value("userRoutingKey")
    private String routing_key;

    private final RabbitTemplate rabbitTemplate;

    private final ThreadLocal<String> tokenThreadLocal = new ThreadLocal<>();

    @Autowired
    public PersonService(PersonRepository personRepository, PasswordEncoder encoder, WebClient.Builder webClientBuilder,  RabbitTemplate rabbitTemplate) {

        this.personRepository = personRepository;
        this.encoder = encoder;
        this.webClientBuilder = webClientBuilder;

        this.rabbitTemplate = rabbitTemplate;
    }

    public void processToken(String token) {
        tokenThreadLocal.set(token);
    }

    public String getToken() {
        return tokenThreadLocal.get();
    }

    public List<PersonDTO> findPersons() {
        List<Person> personList = personRepository.findAll();
        return personList.stream()
                .filter(person -> person.getRole().equals("USER"))
                .map(PersonBuilder::toPersonDTO)
                .collect(Collectors.toList());
    }

    public Person findPersonByEmail(String email){
        Person person = personRepository.findPersonByEmail(email);
        return person;
    }

    public PersonDetailsDTO findPersonById(UUID id) {
        Optional<Person> prosumerOptional = personRepository.findById(id);
        if (!prosumerOptional.isPresent()) {
            LOGGER.error("Person with id {} was not found in db", id);
            throw new ResourceNotFoundException(Person.class.getSimpleName() + " with id: " + id);
        }
        return PersonBuilder.toPersonDetailsDTO(prosumerOptional.get());
    }

    public UUID insert(PersonDetailsDTO personDTO) {
        Person person = PersonBuilder.toEntity(personDTO, "USER");

        System.out.println(person.toString());
        person.setPassword(encoder.encode(personDTO.getPassword()));

        person = personRepository.save(person);
        LOGGER.debug("Person with id {} was inserted in db", person.getId());
        String deviceServiceURL = "http://device-microservice:8082/mapping/insertUser";
        ResponseEntity<?> responseEntity = webClientBuilder.build()
                .post()
                .uri(deviceServiceURL)
                .header("Authorization", "Bearer " + this.tokenThreadLocal.get())
                .body(Mono.just(person), Person.class)
                .retrieve()
                .toBodilessEntity()
                .block();
        if(!responseEntity.getStatusCode().equals(HttpStatus.CREATED)){
            throw new ResourceNotFoundException(Person.class.getSimpleName() + " with id: " + person.getId());
        }

        UserDTO userDTOToSend = PersonBuilder.personToUserDTO(person);
        JSONObject jsonMessage = new JSONObject()
                .put("user", new JSONObject(userDTOToSend))
                .put("action", "insert");
        rabbitTemplate.convertAndSend(exchange, routing_key, jsonMessage.toString());

        return person.getId();
    }

    public void delete(UUID id){
        Optional<Person> personOptional = personRepository.findById(id);
        if (!personOptional.isPresent()) {
            LOGGER.error("Person with id {} was not found in db", id);
            throw new ResourceNotFoundException(Person.class.getSimpleName() + " with id: " + id);
        }

        personRepository.delete(personOptional.get());
        UUID userId = personOptional.get().getId();
        String deviceServiceURL = "http://device-microservice:8082/mapping/deletePersonMapping/" + userId.toString();
        ResponseEntity<?> responseEntity = webClientBuilder.build()
                .delete()
                .uri(deviceServiceURL)
                .header("Authorization", "Bearer " + this.tokenThreadLocal.get())
                .retrieve()
                .toBodilessEntity()
                .block();
        if(!responseEntity.getStatusCode().equals(HttpStatus.OK)){
            LOGGER.error("Person with id {} was not updated in device microservice", id);
            throw new ResourceNotFoundException(PersonDevice.class.getSimpleName() + " with id: " + id);
        }

        JSONObject jsonMessage = new JSONObject()
                .put("user_id", new JSONObject(new IdDTO(id)))
                .put("action", "delete");
        rabbitTemplate.convertAndSend(exchange, routing_key, jsonMessage.toString());

    }

    public Person update(PersonDetailsDTO personDetailsDTO){
        UUID id = personDetailsDTO.getId();
        Optional<Person> personOptional = personRepository.findById(id);
        if (!personOptional.isPresent()) {
            LOGGER.error("Person with id {} was not found in db", id);
            throw new ResourceNotFoundException(Person.class.getSimpleName() + " with id: " + id);
        }

        String deviceServiceURL = "http://device-microservice:8082/mapping/updateUserDetails";
        ResponseEntity<PersonDevice>responseEntity =webClientBuilder.build()
                .put()
                .uri(deviceServiceURL)
                .header("Authorization", "Bearer " + this.tokenThreadLocal.get())
                .body(Mono.just(personDetailsDTO), PersonDetailsDTO.class) // Send personDetailsDTO in the request body
                .retrieve()
                .toEntity(PersonDevice.class)
                .block();
        if(!responseEntity.getStatusCode().equals(HttpStatus.OK)){
            LOGGER.error("Person with id {} was not updated in device microservice", id);
            throw new ResourceNotFoundException(PersonDevice.class.getSimpleName() + " with id: " + id);
        }

        personRepository.update(id,personDetailsDTO.getAddress(),personDetailsDTO.getName(), personDetailsDTO.getEmail());
        Optional<Person> updatedPersonOptional = personRepository.findById(id);
        if (!updatedPersonOptional.isPresent()) {
            LOGGER.error("Person with id {} was not found in db", id);
            throw new ResourceNotFoundException(Person.class.getSimpleName() + " with id: " + id);
        }
        JSONObject jsonMessage = new JSONObject()
                .put("user", new JSONObject(personDetailsDTO))
                .put("action", "update");
        rabbitTemplate.convertAndSend(exchange, routing_key, jsonMessage.toString());

        return updatedPersonOptional.get();

    }
    @Transactional
    public void updateRole(PersonDetailsDTO admin){
        personRepository.updateRole(admin.getEmail(), "ADMIN");
        //PersonDetailsDTO person = findPersonById(admin.getId());
        System.out.println(admin.getId());
        Person person = personRepository.findPersonByEmail(admin.getEmail());
        UserDTO userDTO =  PersonBuilder.personToUserDTO(person);
        JSONObject jsonMessage = new JSONObject()
                .put("user", new JSONObject(userDTO))
                .put("action", "update");
        rabbitTemplate.convertAndSend(exchange, routing_key, jsonMessage.toString());
    }

    public boolean existsAdmin(PersonDetailsDTO admin){
        return personRepository.existsByEmail(admin.getEmail());
    }

}
