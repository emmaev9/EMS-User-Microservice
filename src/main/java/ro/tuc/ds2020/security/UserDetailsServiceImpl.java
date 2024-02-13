package ro.tuc.ds2020.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ro.tuc.ds2020.entities.Person;
import ro.tuc.ds2020.repositories.PersonRepository;
import javax.transaction.Transactional;


@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final PersonRepository personRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        final Person user = personRepository.findPersonByEmail(email);
        if (user != null) {
            return UserDetailsImpl.build(user);
        } else {
            throw new UsernameNotFoundException("Invalid email or password.");
        }
    }
}