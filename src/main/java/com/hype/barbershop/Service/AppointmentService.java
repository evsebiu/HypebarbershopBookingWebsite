package com.hype.barbershop.Service;


import com.hype.barbershop.Exceptions.RuntimeException;
import com.hype.barbershop.Model.DTO.AppointmentDTO;
import com.hype.barbershop.Model.Entity.Appointment;
import com.hype.barbershop.Model.Entity.Barber;
import com.hype.barbershop.Model.Entity.ServiceDetails;
import com.hype.barbershop.Model.Mapper.AppointmentMapper;
import com.hype.barbershop.Repository.AppointmentRepository;
import com.hype.barbershop.Repository.BarberRepository;
import com.hype.barbershop.Repository.ServiceDetailsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor

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


       List<AppointmentDTO> appointments = appointmentRepository.findByEmail(clientEmail)
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
               .orElseThrow(()-> new RuntimeException("Frizerul cu id " + appointmentDTO.getBarberId() + " nu exista."));
        ServiceDetails serviceDetails = serviceDetailsRepository.findById(appointmentDTO.getServiceId())
                .orElseThrow(()-> new RuntimeException("Serviciul cu id " + appointmentDTO.getServiceId() + " nu exista"));

        // 2. calculate when appointment begins and when it finishes.
        // we got the start from the client and duration from service table
        LocalDateTime newStart = appointmentDTO.getStartTime();
        LocalDateTime newEnd = newStart.plusMinutes(serviceDetails.getDuration());

        // 3. chcck for conflicts (optimized : only fetch appointments for this barber)
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
                .orElseThrow(()-> new RuntimeException("Programarea cautata nu exista " + id));

        //validate if barber and service exists

        Barber barber = barberRepository.findById(appointmentDTO.getBarberId())
                .orElseThrow(()-> new RuntimeException("Frizerul cautat nu exista. ID:" + appointmentDTO.getBarberId()));

        ServiceDetails serviceDetails = serviceDetailsRepository.findById(appointmentDTO.getServiceId())
                .orElseThrow(()-> new RuntimeException("Serviciul cautat nu exista. ID:" + appointmentDTO.getServiceId()));

        //calculate next time window
        LocalDateTime newStart = appointmentDTO.getStartTime();
        LocalDateTime newEnd = newStart.plusMinutes(serviceDetails.getDuration());


        //check for appointment conflicts. filter by the target barber and ensure we don't compare appointments with itself
        List<Appointment> barberAppointment  = appointmentRepository.findByBarberId(barber.getId());

        checkForOverlaps(barberAppointment, newStart, newEnd, null);


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
            throw new RuntimeException("Programarea solicitata nu exista.");
        }

        appointmentRepository.deleteById(id);

    }


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
                throw new RuntimeException("Intervalul orar este deja ocupat pentru acest frizer.");
            }
        }
    }
}
