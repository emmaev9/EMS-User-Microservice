package ro.tuc.ds2020.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ro.tuc.ds2020.entities.Person;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PersonRepository extends JpaRepository<Person, UUID> {

    List<Person> findByName(String name);

    Person findPersonByEmail(String email);

    @Query(value = "SELECT p " +
            "FROM Person p " +
            "WHERE p.name = :name " +
            "AND p.age >= 60  ")
    Optional<Person> findSeniorsByName(@Param("name") String name);

    @Modifying
    @Query(value = "UPDATE Person p SET p.name = :name, p.address = :address, p.email = :email WHERE p.id = :id")
    void update(@Param("id")UUID id, @Param("name")String name, @Param("address")String address, @Param("email")String email);

    @Modifying
    @Query(value = "UPDATE Person p SET p.role= :role where p.email= :email ")
    void updateRole(@Param("email")String email, @Param("role")String role);

    @Query(value = "Select p from Person p where p.role = :role" )
    List<Person> findALlUsers(@Param("role") String role);

    boolean existsByEmail(String email);

}