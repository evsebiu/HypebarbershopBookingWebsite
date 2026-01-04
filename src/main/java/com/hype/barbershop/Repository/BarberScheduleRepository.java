package com.hype.barbershop.Repository;

import com.hype.barbershop.Model.Entity.BarberSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

public interface BarberScheduleRepository extends JpaRepository<BarberSchedule, Long> {

    Optional<BarberSchedule> findByBarberIdAndDayOfWeek(Long barberId, DayOfWeek dayOfWeek );
    List<BarberSchedule> findByBarberId(Long barberId);

}
