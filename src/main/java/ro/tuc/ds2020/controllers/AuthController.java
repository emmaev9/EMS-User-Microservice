package ro.tuc.ds2020.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ro.tuc.ds2020.dtos.JwtResponse;
import ro.tuc.ds2020.dtos.LoginRequestDTO;
import ro.tuc.ds2020.dtos.PersonDTO;
import ro.tuc.ds2020.entities.Person;
import ro.tuc.ds2020.security.JwtUtils;
import ro.tuc.ds2020.security.UserDetailsImpl;
import ro.tuc.ds2020.services.PersonService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final PersonService personService;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser( @RequestBody LoginRequestDTO loginRequest) {


        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken(authentication);
        personService.processToken(jwt);
        System.out.println(loginRequest.getEmail());
        System.out.println(loginRequest.getPassword());

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        //Person currentPerson = personService.findPersonByEmail(userDetails.getEmail());
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        if(roles.contains("USER")) {
            return ResponseEntity.ok(new JwtResponse(jwt,
                    userDetails.getId(),
                    userDetails.getPassword(),
                    userDetails.getEmail(),
                    "USER"));

        }
        else {
            return ResponseEntity.ok(new JwtResponse(jwt,
                    userDetails.getId(),
                    userDetails.getPassword(),
                    userDetails.getEmail(),
                    "ADMIN"));
        }

    }
}