package com.hype.barbershop.Service;


import com.hype.barbershop.Exceptions.BarbershopException;
import com.hype.barbershop.Model.DTO.AppointmentDTO;
import com.hype.barbershop.Model.Entity.Appointment;
import com.hype.barbershop.Model.Entity.Barber;
import com.hype.barbershop.Model.Entity.ServiceDetails;
import com.hype.barbershop.Model.Mapper.AppointmentMapper;
import com.hype.barbershop.Repository.AppointmentRepository;
import com.hype.barbershop.Repository.BarberRepository;
import com.hype.barbershop.Repository.ServiceDetailsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.LocalDate;
import java.util.ArrayList;

import java.time.LocalDateTime;
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

    public AppointmentService(AppointmentMapper appointmentMapper, AppointmentRepository appointmentRepository,
                              ServiceDetailsRepository serviceDetailsRepository,
                              BarberRepository barberRepository){
        this.appointmentMapper=appointmentMapper;
        this.appointmentRepository=appointmentRepository;
        this.serviceDetailsRepository=serviceDetailsRepository;
        this.barberRepository=barberRepository;
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
    public AppointmentDTO updateAppointment  (Long id, AppointmentDTO appointmentDTO){

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
            LocalTime workStart = LocalTime.of(10, 0); // opens at 10am
            LocalTime workEnd = LocalTime.of(18, 0); // closes at 18pm

        // find service duration
        ServiceDetails service = serviceDetailsRepository.findById(serviceId)
                .orElseThrow(()-> new BarbershopException("Serviciu inexistent"));

        int durationMinutes = service.getDuration();

        // collect all barber's appointments for required day
        // for now we will use a simplified logic that filters in memory(it's not good for large databases)
        List<Appointment> existingAppointment = appointmentRepository.findByBarberIdAndDate(barberId, date)
                .stream()
                .filter(a-> a.getStartTime().toLocalDate().equals(date))
                .collect(Collectors.toList());

        List<String> slots = new ArrayList<>();
        LocalTime currentSlot = workStart;

        //generate slots every 30 minutes
        while(currentSlot.plusMinutes(durationMinutes).isBefore(workEnd) || currentSlot.plusMinutes(durationMinutes).equals(workEnd)){
            LocalTime slotEnd = currentSlot.plusMinutes(durationMinutes);
            LocalDateTime slotStartDateTime = LocalDateTime.of(date, currentSlot);
            LocalDateTime slotEndDateTime = LocalDateTime.of(date, slotEnd);

            boolean isOccupied = false;

            //verify overlapping with existing appointments

            for (Appointment app : existingAppointment){
                LocalDateTime appStart = app.getStartTime();
                LocalDateTime appEnd = appStart.plusMinutes(app.getServiceDetails().getDuration());

                if (slotStartDateTime.isBefore(appEnd) && slotEndDateTime.isAfter(appStart)){
                    isOccupied = true;
                    break;
                }
            }

            if (!isOccupied){
                slots.add(currentSlot.toString()); // format type : 10:00, 10:30, 11:00 etc
            }

            // skip every 30 mins

            currentSlot = currentSlot.plusMinutes(30);
        }

        return slots;
    }
}
