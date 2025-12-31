package com.hype.barbershop.Repository;

import com.hype.barbershop.Model.Entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByBarberIdAndStartTimeBetween(Long barberId, LocalDateTime start, LocalDateTime end);
    List<Appointment> findByClientName(String clientName);
    List<Appointment> findByPhoneNumber(String phoneNumber);
    List<Appointment> findByClientEmail(String clientEmail);
    List<Appointment> findByBarberId(Long barberId);
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "FROM Appointment a " +
            "WHERE a.barber.id = :barberId " +
            "AND (:excludeId IS NULL OR a.id <> :excludeId) " +
            "AND a.startTime < :newEnd " +
            "AND FUNCTION('TIMESTAMPADD', MINUTE, a.serviceDetails.duration, a.startTime) > :newStart")
    boolean existsOverlappingAppointment(Long barberId, LocalDateTime newStart, LocalDateTime newEnd, Long excludeId);


}
