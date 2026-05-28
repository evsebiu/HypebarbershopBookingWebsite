package com.hype.barbershop.Repository;

import com.hype.barbershop.Model.Entity.BarberDayOff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BarberDayOffRepository extends JpaRepository<BarberDayOff, Long> {
    boolean existsByBarberIdAndDate(Long barberId,  LocalDate date);
    List<BarberDayOff> findByBarberIdAndDateGreaterThanEqualOrderByDateAsc(Long barberId, LocalDate date);
}
