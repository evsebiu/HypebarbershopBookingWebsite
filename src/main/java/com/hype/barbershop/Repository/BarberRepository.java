package com.hype.barbershop.Repository;

import com.hype.barbershop.Model.Entity.Barber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BarberRepository extends JpaRepository<Barber, Long> {


    List<Barber> findByIsActiveTrue();
    Optional<Barber> findByEmail(String email);
    List<Barber> findByFirstName(String firstName);
    List<Barber> findByLastName(String lastName);
    boolean existsEmailAndIdNot(String email, Long id);
}
