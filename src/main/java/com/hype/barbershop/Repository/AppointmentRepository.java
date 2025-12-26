package com.hype.barbershop.Repository;

import com.hype.barbershop.Model.Entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByClientName(String clientName);
    List<Appointment> findByPhoneNumber(String phoneNumber);
    List<Appointment> findByEmail(String clientEmail);
    List<Appointment> findByBarberId(Long barberId);

}
