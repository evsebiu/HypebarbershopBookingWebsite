package com.hype.barbershop.Repository;

import com.hype.barbershop.Model.Entity.Appointment;
import com.hype.barbershop.Model.Entity.Barber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findById(Long id);
    List<Appointment> findByClientName(String clientName);
    List<Appointment> findByPhoneNumber(String phoneNumber);
    Optional<Appointment> findByEmail(String clientEmail);

}
