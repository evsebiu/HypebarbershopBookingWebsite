package com.hype.barbershop.Service;

import com.hype.barbershop.Exceptions.BarbershopException;
import com.hype.barbershop.Model.DTO.BarberDTO;
import com.hype.barbershop.Model.DTO.BarberDayOffDTO;
import com.hype.barbershop.Model.Entity.Appointment;
import com.hype.barbershop.Model.Entity.Barber;
import com.hype.barbershop.Model.Entity.BarberDayOff;
import com.hype.barbershop.Model.Enums.AppointmentStatus;
import com.hype.barbershop.Repository.AppointmentRepository;
import com.hype.barbershop.Repository.BarberDayOffRepository;
import com.hype.barbershop.Repository.BarberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class BarberDayOffService {

    private final BarberDayOffRepository dayOffRepository;

    private final BarberRepository barberRepository;

    private final AppointmentRepository appointmentRepository;




    @Transactional
    public BarberDayOffDTO addDayOff(BarberDayOffDTO dto, String email) {
        Barber barber = barberRepository.findByEmail(email)
                .orElseThrow(() -> new BarbershopException("Frizerul nu a fost gasit in baza de date."));

        if (dto.getDate().isBefore(LocalDate.now())) {
            throw new BarbershopException("Nu îți poți seta zile libere în trecut");
        }

        if (dayOffRepository.existsByBarberIdAndDate(barber.getId(), dto.getDate())) {
            throw new BarbershopException("Ziua selectata este deja marcata ca libera");
        }


        LocalDateTime startOfDay = dto.getDate().atStartOfDay();
        LocalDateTime endOfDay = dto.getDate().atTime(LocalTime.MAX);

        List<Appointment> existingAppointments =
                appointmentRepository.findByBarberIdAndStartTimeBetween(barber.getId(), startOfDay, endOfDay)
                        .stream()
                        .filter(app -> app.getStatus() != AppointmentStatus.CANCELED)
                        .toList();

        if (!existingAppointments.isEmpty()) {
            throw new BarbershopException("Nu poti lua o zi libera, ai " + existingAppointments.size() +
                    "programare/programari pe data " + dto.getDate() + " anuleaza / reprogrameaza intai");
        }
        BarberDayOff dayOff = new BarberDayOff();
        dayOff.setBarber(barber);
        dayOff.setDate(dto.getDate());
        dayOff.setReason(dto.getReason());

        BarberDayOff saved = dayOffRepository.save(dayOff);

        dto.setId(saved.getId());
        dto.setBarberId(barber.getId());
        return dto;
    }

    @Transactional(readOnly = true)
    public List<BarberDayOffDTO> getFutureDaysOff(String email) {
        Barber barber = barberRepository.findByEmail(email)
                .orElseThrow(() -> new BarbershopException("Frizerul nu a fost găsit."));

        return dayOffRepository.findByBarberIdAndDateGreaterThanEqualOrderByDateAsc(barber.getId(), LocalDate.now())
                .stream()
                .map(d -> {
                    BarberDayOffDTO dto = new BarberDayOffDTO();
                    dto.setId(d.getId());
                    dto.setBarberId(d.getBarber().getId());
                    dto.setDate(d.getDate());
                    dto.setReason(d.getReason());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void delteDayOff(Long dayOffId, String email){

        Barber barber = barberRepository.findByEmail(email)
                .orElseThrow(()-> new BarbershopException("Ziua selectata nu apartine contului tau"));

        BarberDayOff barberDayOff = dayOffRepository.findById(dayOffId)
                .orElseThrow(()-> new BarbershopException(" Ziua selectata nu este marcata ca libera."));

        if (!barberDayOff.getBarber().getId().equals(barber.getId())) {
            throw new BarbershopException("Nu poți șterge ziua liberă a altui frizer!");
        }

    dayOffRepository.delete(barberDayOff);
    }


}
