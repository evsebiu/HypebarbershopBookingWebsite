package com.hype.barbershop.Service;


import com.hype.barbershop.Exceptions.BarbershopException;
import com.hype.barbershop.Exceptions.BarbershopResourceNotFound;
import com.hype.barbershop.Model.DTO.AppointmentDTO;
import com.hype.barbershop.Model.Entity.*;
import com.hype.barbershop.Model.Mapper.AppointmentMapper;
import com.hype.barbershop.Repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.ArrayList;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j

public class AppointmentService {

    private final AppointmentMapper appointmentMapper;
    private final ServiceDetailsRepository serviceDetailsRepository;
    private final AppointmentRepository appointmentRepository;
    private final BarberRepository barberRepository;
    private final BarberScheduleRepository scheduleRepository;
    private final BarberDayOffRepository dayOffRepository;

    public AppointmentService(AppointmentMapper appointmentMapper, AppointmentRepository appointmentRepository,
                              ServiceDetailsRepository serviceDetailsRepository,
                              BarberRepository barberRepository,
                              BarberScheduleRepository scheduleRepository,
                              BarberDayOffRepository dayOffRepository){
        this.appointmentMapper=appointmentMapper;
        this.appointmentRepository=appointmentRepository;
        this.serviceDetailsRepository=serviceDetailsRepository;
        this.barberRepository=barberRepository;
        this.scheduleRepository=scheduleRepository;
        this.dayOffRepository=dayOffRepository;
    }


    //GET
    @Transactional(readOnly = true)
    public List<AppointmentDTO> getAllAppointments(){

        log.debug("Se cer toate programarile....");

        List<AppointmentDTO> appointments = appointmentRepository.findAll()
                .stream()
                .map(appointmentMapper::toDTO)
                .collect(Collectors.toList());

        log.info("S-au returnat {} programari catre dashboard", appointments.size());

        return appointments;

    }


    @Transactional(readOnly = true)
    public Optional<AppointmentDTO> getById(Long id){

        log.debug("Se cauta programarea cu ID: {} ", id);

        return appointmentRepository.findById(id)
                .map(appointmentMapper::toDTO)
                .or(()-> {
                    log.warn("Programarea cu ID {} a fost solicitata, dar nu exista in baza de date" , id);
                    return Optional.empty();
                });
    }

    @Transactional(readOnly = true)
    public List<AppointmentDTO> getByClientName(String clientName){

        log.debug("Se cere programare cu numele clientului {} ", clientName);

        List<AppointmentDTO> appointments = appointmentRepository.findByClientName(clientName)
                .stream()
                .map(appointmentMapper::toDTO)
                .collect(Collectors.toList());

        if (appointments.isEmpty()){
            log.info("Nu exista programari pentru clientul {}", clientName);
        } else {
            log.info("S-a returnat programarea clientului cu numele {}", clientName);
        }

        return appointments;

    }


    @Transactional(readOnly = true)
    public List<AppointmentDTO> getByPhoneNumber(String phoneNumber){

        log.debug("Se cauta clientul cu numarul de telefon {}", phoneNumber);

        List<AppointmentDTO> appointments = appointmentRepository.findByPhoneNumber(phoneNumber)
                .stream()
                .map(appointmentMapper::toDTO)
                .collect(Collectors.toList());

        if (appointments.isEmpty()){
            log.info("Nu exista niciun client cu numarul {} in baza de date", phoneNumber);
        } else {
            log.info("S-a returnat programarea clientului care are numarul de telefon {}", phoneNumber);
        }

        return appointments;
    }

    @Transactional(readOnly = true)
    public List<AppointmentDTO> getByEmail(String clientEmail){

        log.debug("Se solicita clientul cu emailul {} ", clientEmail);


       List<AppointmentDTO> appointments = appointmentRepository.findByClientEmail(clientEmail)
               .stream()
               .map(appointmentMapper::toDTO)
               .collect(Collectors.toList());
       if (appointments.isEmpty()){
           log.info("Nu exista nicio programare cu emailul {} ", clientEmail);
       } else {
           log.info("S-a returnat programarea cu emailul {}", clientEmail);
       }

       return appointments;

    }


    @Transactional
    public AppointmentDTO createAppointment(AppointmentDTO appointmentDTO){
        log.info("Incercare de creare de programare: {} ", appointmentDTO.getClientName());


        if (appointmentDTO.getStartTime().isBefore(LocalDateTime.now().minusMinutes(5))){
            throw new BarbershopException("Nu se pot efectua programari in trecut! ");
        }

        // 1. first step check if barber and service exists in database.
       Barber barber = barberRepository.findById(appointmentDTO.getBarberId())
               .orElseThrow(()-> new BarbershopException("Frizerul cu id " + appointmentDTO.getBarberId() + " nu exista."));
        ServiceDetails serviceDetails = serviceDetailsRepository.findById(appointmentDTO.getServiceId())
                .orElseThrow(()-> new BarbershopException("Serviciul cu id " + appointmentDTO.getServiceId() + " nu exista"));

        // 2. calculate when appointment begins and when it finishes.
        // we got the start from the client and duration from service table
        LocalDateTime newStart = appointmentDTO.getStartTime();
        LocalDateTime newEnd = newStart.plusMinutes(serviceDetails.getDuration());

        // 3. check for conflicts (optimized : only fetch appointments for this barber)
        List<Appointment> barberAppointments = appointmentRepository.findByBarberId(barber.getId());

        checkForOverlaps(barberAppointments, newStart, newEnd, null);

        //save
        Appointment appointment = appointmentMapper.toEntity(appointmentDTO);
        appointment.setBarber(barber);
        appointment.setServiceDetails(serviceDetails);

        Appointment savedAppointment = appointmentRepository.save(appointment);

        log.info("Programare facuta cu succes, ID: {}", savedAppointment.getId());

        return appointmentMapper.toDTO(savedAppointment);

    }


    //update method available only for administrator / barber
    public AppointmentDTO updateAppointmentAPI (Long id, AppointmentDTO appointmentDTO){

        log.info("Incercare de actualizare a programarii cu id: {} ", id);

        //check if appointment exists
        Appointment existingAppointment = appointmentRepository.findById(id)
                .orElseThrow(()-> new BarbershopException("Programarea cautata nu exista " + id));

        //validate if barber and service exists

        Barber barber = barberRepository.findById(appointmentDTO.getBarberId())
                .orElseThrow(()-> new BarbershopException("Frizerul cautat nu exista. ID:" + appointmentDTO.getBarberId()));

        ServiceDetails serviceDetails = serviceDetailsRepository.findById(appointmentDTO.getServiceId())
                .orElseThrow(()-> new BarbershopException("Serviciul cautat nu exista. ID:" + appointmentDTO.getServiceId()));

        //calculate next time window
        LocalDateTime newStart = appointmentDTO.getStartTime();
        LocalDateTime newEnd = newStart.plusMinutes(serviceDetails.getDuration());


        //check for appointment conflicts. filter by the target barber and ensure we don't compare appointments with itself
        List<Appointment> barberAppointment  = appointmentRepository.findByBarberId(barber.getId());

        checkForOverlaps(barberAppointment, newStart, newEnd, id);


        // Update fields

        existingAppointment.setClientName(appointmentDTO.getClientName());
        existingAppointment.setClientEmail(appointmentDTO.getClientEmail());
        existingAppointment.setPhoneNumber(appointmentDTO.getPhoneNumber());
        existingAppointment.setAdditionalInfo(appointmentDTO.getAdditionalInfo());
        existingAppointment.setStartTime(newStart);
        existingAppointment.setBarber(barber);
        existingAppointment.setServiceDetails(serviceDetails);

        //save

        Appointment savedAppointment = appointmentRepository.save(existingAppointment);
        log.info("S-a actualizat programarea cu ID {} ", savedAppointment.getId());

        return appointmentMapper.toDTO(savedAppointment);

    }


    public void deleteAppointment (Long id){
        log.info("Se solicita stergerea programarii cu ID {} ", id);

        if (!appointmentRepository.existsById(id)){
            throw new BarbershopException("Programarea solicitata nu exista.");
        }

        appointmentRepository.deleteById(id);

    }

    /**
     * Checks if an appointment overlaps with an existing one for a specific barber.
     * * Logic: Two time intervals (StartA, EndA) and (StartB, EndB) overlap if:
     * StartA < EndB AND EndA > StartB
     * * @param barberId The ID of the barber
     * @param newStart The requested start time
     * @param newEnd   The requested end time (calculated as start + duration)
     * @param excludeId The ID of the appointment to ignore (used for updates). Pass -1 or null for creation.
     * @return true if an overlap exists
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "FROM Appointment a " +
            "JOIN a.serviceDetails s " +
            "WHERE a.barber.id = :barberId " +
            "AND (:excludeId IS NULL OR a.id <> :excludeId) " +
            "AND (a.startTime < :newEnd " +
            "AND FUNCTION('TIMESTAMPADD', MINUTE, s.duration, a.startTime) > :newStart)")

    //helper method to check for overlaps ( changed initial createAppointment & updateAppointment to make them better)
    private void checkForOverlaps(List<Appointment> appointments, LocalDateTime newStart, LocalDateTime newEnd,
                                  Long excludeId){
        for (Appointment existing : appointments){
            if (excludeId != null && existing.getId().equals(excludeId)){
                continue;
            }

            LocalDateTime existingStart = existing.getStartTime();
            LocalDateTime existingEnd = existingStart.plusMinutes(existing.getServiceDetails().getDuration());

            // Overlap logic : Start A < End B & Start B < End A
            if (newStart.isBefore(existingEnd) && existingStart.isBefore(newEnd)){
                log.warn("Conflict de programari pentru frizerul cu ID {} ", existing.getBarber().getId());
                throw new BarbershopException("Intervalul orar este deja ocupat pentru acest frizer.");
            }
        }
    }


    // CALENDAR BOOOKING AVAILABLE SLOTS

    public List<String> getAvailableSlots(Long barberId, Long serviceId, LocalDate date){
        // 1. Verificăm dacă este o zi liberă specială (Concediu)
        if (dayOffRepository.existsByBarberIdAndDate(barberId, date)){
            return new ArrayList<>(); // Zi liberă -> nicio oră disponibilă
        }

        LocalTime workStart = null;
        LocalTime workEnd = null;

        // 2. Căutăm programul în baza de date pentru ziua respectivă (Luni, Marți...)
        Optional<BarberSchedule> schedulOpt =
                scheduleRepository.findByBarberIdAndDayOfWeek(barberId, date.getDayOfWeek());

        if (schedulOpt.isPresent()){
            BarberSchedule schedule = schedulOpt.get();

            // Dacă ziua este marcată explicit ca nelucrătoare în setările frizerului
            if (Boolean.FALSE.equals(schedule.getIsWorkingDay())){
                return new ArrayList<>();
            }

            workStart = schedule.getStartTime();
            workEnd = schedule.getEndTime();
        }

        // --- FIX CRITIC (FALLBACK) ---
        // Dacă nu s-a găsit orar (frizerul nu a setat nimic)
        // SAU dacă orele din bază sunt corupte (null), folosim orarul standard.
        if (workStart == null || workEnd == null) {
            // Regula default: Luni Închis
            if (date.getDayOfWeek() == DayOfWeek.MONDAY){
                return new ArrayList<>();
            }
            // Regula default: Marți-Duminică 10:00 - 20:00
            workStart = LocalTime.of(10, 0);
            workEnd = LocalTime.of(20, 0);
        }
        // -----------------------------

        // 3. Găsim durata serviciului
        ServiceDetails service = serviceDetailsRepository.findById(serviceId)
                .orElseThrow(() -> new BarbershopException("Serviciul inexistent"));

        int durationMinutes = service.getDuration();
        int stepMinutes = 15; // Intervalul dintre sloturi

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        // 4. Luăm programările existente pentru a nu ne suprapune
        List<Appointment> existingAppointment = appointmentRepository.findByBarberIdAndStartTimeBetween(barberId, startOfDay, endOfDay);

        List<String> slots = new ArrayList<>();
        LocalTime currentSlot = workStart;

        // 5. Algoritmul de generare a sloturilor
        while (!currentSlot.plusMinutes(durationMinutes).isAfter(workEnd)) {
            LocalTime slotEnd = currentSlot.plusMinutes(durationMinutes);

            LocalDateTime proposedStart = LocalDateTime.of(date, currentSlot);
            LocalDateTime proposedEnd = LocalDateTime.of(date, slotEnd);

            boolean isOccupied = false;

            // Nu permitem programări în trecut (dacă data selectată e azi)
            if (date.equals(LocalDate.now()) && currentSlot.isBefore(LocalTime.now())) {
                isOccupied = true;
            }

            // Verificăm suprapunerea cu programările existente
            if (!isOccupied) {
                for (Appointment app : existingAppointment) {
                    LocalDateTime appStart = app.getStartTime();
                    // Calculăm finalul programării existente bazat pe durata serviciului ei
                    LocalDateTime appEnd = appStart.plusMinutes(app.getServiceDetails().getDuration());

                    // Logică de suprapunere: (StartA < EndB) și (EndA > StartB)
                    if (proposedStart.isBefore(appEnd) && proposedEnd.isAfter(appStart)) {
                        isOccupied = true;
                        break;
                    }
                }
            }

            // Dacă e liber, adăugăm în listă
            if (!isOccupied) {
                slots.add(currentSlot.toString());
            }

            currentSlot = currentSlot.plusMinutes(stepMinutes);
        }

        return slots;

    }

    @Transactional(readOnly = true)
    public List<AppointmentDTO> getAllAppointmentsForCurrentBarber(String email){
        Barber barber =  barberRepository.findByEmail(email)
                .orElseThrow(()-> new BarbershopException("Frizerul negasit."));


        return appointmentRepository.findByBarberId(barber.getId())
                .stream()
                .map(appointmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentDTO> getAppointmentsForBarber(String email){

        // find barber after email to get his ID

        Barber barber = barberRepository.findByEmail(email)
                .orElseThrow(()-> new BarbershopException("Frizerul nu a fost gasit."));

        // use method from repo that filters after id

        return appointmentRepository.findByBarberId(barber.getId())
                .stream()
                .map(appointmentMapper::toDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<AppointmentDTO> getAppointmentsForBarberByDate(String email, LocalDate date){

        // identify logged-in barber

        Barber barber = barberRepository.findByEmail(email)
                .orElseThrow(()-> new BarbershopResourceNotFound("Frizerul nu a fost gasit"));

        // calculate day time
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        // search in database
        List<Appointment> appointments = appointmentRepository.findByBarberIdAndStartTimeBetween(
                barber.getId(),
                startOfDay,
                endOfDay
        );

        return appointments.stream()
                .sorted(Comparator.comparing(Appointment::getStartTime))
                .map(appointmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    // helper method for security check
    @Transactional(readOnly = true)
    public AppointmentDTO getAppointmentForEdit(Long id, String requesterEmail, boolean isAdmin){
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(()-> new BarbershopResourceNotFound("Programarea solicitata nu exista"));

        // first version of this method is to check only for admin
        if (!isAdmin && !appointment.getBarber().getEmail().equals(requesterEmail)){
            throw new BarbershopException("Nu ai permisiunea sa modifici aceasta programare");
        }

        return appointmentMapper.toDTO(appointment);

    }

    @Transactional
    public AppointmentDTO updateAppointment(Long id, AppointmentDTO appointmentDTO, String requesterEmail, boolean isAdmin){


        // find original entity
        Appointment existingApp = appointmentRepository.findById(id)
                .orElseThrow(()-> new BarbershopResourceNotFound("Programarea solicitata nu exista"));

        //permission check
        if (!isAdmin && !existingApp.getBarber().getEmail().equals(requesterEmail)){
            throw new BarbershopException("ACCEZ INTERZIS! Nu aveti permisiunea pentru a modifica aceasta programare");
        }

        // verify logic, if duration changed, we check overlapping by calculating new period
        ServiceDetails service = serviceDetailsRepository.findById(appointmentDTO.getServiceId())
                .orElseThrow(()-> new BarbershopException("Serviciul solicitat nu exista"));

        LocalDateTime newStart = appointmentDTO.getStartTime();
        LocalDateTime newEnd = newStart.plusMinutes(service.getDuration());

        // verify conflicts and we exclude current APPOINTMENT ID
        List<Appointment> barberAppointments = appointmentRepository.findByBarberId(existingApp.getBarber().getId());
        checkForOverlaps(barberAppointments, newStart, newEnd, id);

        // update entity
        existingApp.setClientEmail(appointmentDTO.getClientEmail());
        existingApp.setClientName(appointmentDTO.getClientName());
        existingApp.setPhoneNumber(appointmentDTO.getPhoneNumber());
        existingApp.setStartTime(appointmentDTO.getStartTime());

        // relations
        existingApp.setServiceDetails(service);


        if (appointmentDTO.getStatus() != null) {
            existingApp.setStatus(appointmentDTO.getStatus());
        }

        Appointment saved = appointmentRepository.save(existingApp);

        log.info("Programarea {} a fost actualizata de {}", id, requesterEmail);

        return appointmentMapper.toDTO(saved);
    }

}
