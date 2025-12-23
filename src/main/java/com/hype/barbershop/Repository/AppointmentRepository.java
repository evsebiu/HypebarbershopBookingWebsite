package com.hype.barbershop.Repository;

import com.hype.barbershop.Model.Entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

}
