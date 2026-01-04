package com.hype.barbershop.Service;


import com.hype.barbershop.Exceptions.BarbershopDuplicateResource;
import com.hype.barbershop.Exceptions.BarbershopException;
import com.hype.barbershop.Exceptions.IllegalBarbershopArgument;
import com.hype.barbershop.Exceptions.BarbershopResourceNotFound;
import com.hype.barbershop.Model.DTO.BarberDTO;
import com.hype.barbershop.Model.DTO.BarberRegistrationDTO;
import com.hype.barbershop.Model.DTO.DailyScheduleDTO;
import com.hype.barbershop.Model.DTO.WeeklyScheduleDTO;
import com.hype.barbershop.Model.Entity.Barber;
import com.hype.barbershop.Model.Entity.BarberSchedule;
import com.hype.barbershop.Model.Enums.Role;
import com.hype.barbershop.Model.Mapper.BarberMapper;
import com.hype.barbershop.Repository.AppointmentRepository;
import com.hype.barbershop.Repository.BarberRepository;
import com.hype.barbershop.Repository.BarberScheduleRepository;
import com.hype.barbershop.Repository.ServiceDetailsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor

public class BarberService {

    private final BarberMapper barberMapper;
    private final AppointmentRepository appointmentRepo;
    private final BarberRepository barberRepo;
    private final ServiceDetailsRepository serviceDetailsRepo;
    private final PasswordEncoder passwordEncoder;
    private final BarberScheduleRepository scheduleRepo;


    //GET methods

    @Transactional(readOnly = true)
    public List<BarberDTO> getAll() {
        return barberRepo.findAll()
                .stream()
                .map(barberMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<BarberDTO> findById(Long id) {
        return barberRepo.findById(id)
                .map(barberMapper::toDTO)
                .or(() -> {
                    return Optional.empty();
                });
    }

    @Transactional(readOnly = true)
    public List<BarberDTO> getIfActive() {
        log.debug("Se cauta frizerii activi...");

        return barberRepo.findByIsActiveTrue()
                .stream()
                .map(barberMapper::toDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public Optional<BarberDTO> getByEmail(String email) {
        log.debug("Se returneaza frizerul cu emailul {}", email);

        return barberRepo.findByEmail(email)
                .map(barberMapper::toDTO)
                .or(() -> {
                    log.warn("Frizerul cu emailul solicitat nu a fost gasit");
                    return Optional.empty();
                });
    }

    @Transactional(readOnly = true)
    public List<BarberDTO> getByFirstName(String firstName) {
        log.debug("Se solicita frizerii cu first name {} ", firstName);

        return barberRepo.findByFirstName(firstName)
                .stream()
                .map(barberMapper::toDTO)
                .collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public List<BarberDTO> getByLastName(String lastName) {
        log.debug("Se solicita frizerii cu last name{}", lastName);

        return barberRepo.findByLastName(lastName)
                .stream()
                .map(barberMapper::toDTO)
                .collect(Collectors.toList());
    }


    @Transactional
    public BarberDTO createBarber(BarberRegistrationDTO regDTO) {
        // null exception
        if (regDTO == null) {
            throw new IllegalBarbershopArgument("Detaliile frizerului sunt necesare.");
        }

        //check if barber email exists in database because it's unique

        if (regDTO.getEmail() != null && barberRepo.findByEmail(regDTO.getEmail()).isPresent()) {
            throw new BarbershopDuplicateResource("Un frizer cu acest email exista deja in baza de date");
        }

        Barber barber = new Barber();
        barber.setFirstName(regDTO.getFirstName());
        barber.setLastName(regDTO.getLastName());
        barber.setEmail(regDTO.getEmail());
        barber.setIsActive(true);

        // encrypt password before saving
        barber.setPassword(passwordEncoder.encode(regDTO.getRawPassword()));

        //set role
        if (Boolean.TRUE.equals(regDTO.getIsAdmin())) {
            barber.setRole(Role.ROLE_ADMIN);
        } else {
            barber.setRole(Role.ROLE_BARBER);
        }

        Barber saveBarber = barberRepo.save(barber);
        return barberMapper.toDTO(saveBarber);
    }

    @Transactional
    public BarberDTO updateBarber(Long id, BarberDTO barberDTO) {

        // find existing barber
        Barber existingBarber = barberRepo.findById(id)
                .orElseThrow(() -> new BarbershopResourceNotFound("Frizerul cautat nu a fost gasit. ID: " + id));

        //validate input

        if (barberDTO == null) {
            throw new IllegalBarbershopArgument("Detaiile frizerului nu pot fi nulle");
        }

        // check if email is already taken by another barber

        if (!existingBarber.getEmail().equals(barberDTO.getEmail())) {
            boolean emailExists = barberRepo.existsByEmailAndIdNot(barberDTO.getEmail(), existingBarber.getId());
            if (emailExists) {
                throw new IllegalBarbershopArgument("Emailul este deja luat de un alt frizer.");
            }
        }

        //update fields
        existingBarber.setEmail(barberDTO.getEmail());
        existingBarber.setIsActive(barberDTO.getIsActive());
        existingBarber.setFirstName(barberDTO.getFirstName());
        existingBarber.setLastName(barberDTO.getLastName());

        //save
        Barber savedBarber = barberRepo.save(existingBarber);

        return barberMapper.toDTO(savedBarber);
    }


    @Transactional
    public void deleteBarber(Long id) {
        if (id == null) {
            throw new IllegalBarbershopArgument("ID-ul nu poate fi null");
        }

        Barber barberToDelete = barberRepo.findById(id)
                .orElseThrow(() -> new BarbershopResourceNotFound("ID-ul frizerului nu a fost gasit"));
        barberRepo.delete(barberToDelete);
    }

    @Transactional
    public void toggleBarberStatus(Long id) {

        // search barber
        Barber barber = barberRepo.findById(id)
                .orElseThrow(() -> new BarbershopResourceNotFound("Frizerul cu ID" + id + " nu a fost gasit"));

        boolean currentStatus = Boolean.TRUE.equals(barber.getIsActive());
        barber.setIsActive(!currentStatus);

        barberRepo.save(barber);
    }

    @Transactional(readOnly = true)
    public WeeklyScheduleDTO getBarberSchedule(String email) {
        Barber barber = barberRepo.findByEmail(email)
                .orElseThrow(() -> new BarbershopException("Frizerul nu a fost gasit."));

        List<BarberSchedule> schedules = scheduleRepo.findByBarberId(barber.getId());

        WeeklyScheduleDTO weeklyDTO = new WeeklyScheduleDTO();

        // if barber didn't set up his schedule we make a default one Monday - Saturday
        if (schedules.isEmpty()) {
            for (DayOfWeek day : DayOfWeek.values()) {
                DailyScheduleDTO dayDTO = new DailyScheduleDTO();
                dayDTO.setDayOfWeek(day);
                dayDTO.setStartTime(LocalTime.of(10, 0));
                dayDTO.setEndTime(LocalTime.of(20, 0));

                dayDTO.setIsWorkingDay(day != DayOfWeek.MONDAY);
                weeklyDTO.getDailySchedules().add(dayDTO);
            }
        } else {
            // map what exists in db
            for (BarberSchedule entity : schedules) {
                DailyScheduleDTO dayDTO = new DailyScheduleDTO();
                dayDTO.setDayOfWeek(entity.getDayOfWeek());
                dayDTO.setStartTime(entity.getStartTime());
                dayDTO.setEndTime(entity.getEndTime());
                dayDTO.setIsWorkingDay(entity.getIsWorkingDay());
                weeklyDTO.getDailySchedules().add(dayDTO);
            }

            // sort days - monday-saturday
            weeklyDTO.getDailySchedules().sort(Comparator.comparing(DailyScheduleDTO::getDayOfWeek));
        }

        return weeklyDTO;
    }

    @Transactional
    public void updateSchedule(String email, WeeklyScheduleDTO dto) {
        Barber barber = barberRepo.findByEmail(email)
                .orElseThrow(() -> new BarbershopResourceNotFound("Frizerul nu a fost gasit"));

        for (DailyScheduleDTO dayDTO : dto.getDailySchedules()) {
            // search if already exists and entry for day
            BarberSchedule schedule = scheduleRepo.findByBarberIdAndDayOfWeek(barber.getId(), dayDTO.getDayOfWeek())
                    .orElse(new BarberSchedule());

            if (schedule.getId() == null) {
                schedule.setBarber(barber);
                schedule.setDayOfWeek(dayDTO.getDayOfWeek());
            }

            schedule.setStartTime(dayDTO.getStartTime());
            schedule.setEndTime(dayDTO.getEndTime());
            schedule.setIsWorkingDay(dayDTO.getIsWorkingDay() != null ? dayDTO.getIsWorkingDay() : false);

            scheduleRepo.save(schedule);
        }
    }
}


