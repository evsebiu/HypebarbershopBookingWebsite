package com.hype.barbershop.Service;


import com.hype.barbershop.Model.DTO.AppointmentDTO;
import com.hype.barbershop.Model.Entity.Appointment;
import com.hype.barbershop.Model.Mapper.AppointmentMapper;
import com.hype.barbershop.Repository.AppointmentRepository;
import com.hype.barbershop.Repository.BarberRepository;
import com.hype.barbershop.Repository.ServiceDetailsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    public List<AppointmentDTO> getAllAppointments(){

        log.debug("Se cer toate programarile....");

        List<AppointmentDTO> appointments = appointmentRepository.findAll()
                .stream()
                .map(appointmentMapper::toDTO)
                .collect(Collectors.toList());

        log.info("S-au returnat {} programari catre dashboard", appointments.size());

        return appointments;

    }

    public Optional<AppointmentDTO> getById(Long id){

        log.debug("Se cauta programarea cu ID: {} ", id);

        return appointmentRepository.findById(id)
                .map(appointmentMapper::toDTO)
                .or(()-> {
                    log.warn("Programarea cu ID {} a fost solicitata, dar nu exista in baza de date" , id);
                    return Optional.empty();
                });
    }

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



    public AppointmentDTO createAppointment(AppointmentDTO appointmentDTO){
        log.info("Incercare de creare de programare: {} ", appointmentDTO.getClientName());

        // 1. first step check if barber and service exists in database.
        var barber = barberRepository.findById(appointmentDTO.getBarberId())
                .orElseThrow(()-> new RuntimeException("Frizerul cu id: " + appointmentDTO.getBarberId() + " nu exista."));

        var service  = serviceDetailsRepository.findById(appointmentDTO.getServiceId())
                .orElseThrow(()-> new RuntimeException("Serviciul cu id " + appointmentDTO.getServiceId() + " nu exista"));

        // 2. calculate when appointment begins and when it finishes.
        // we got the start from the client and duration from service table
        LocalDateTime newStart = appointmentDTO.getStartTime();
        LocalDateTime newEnd = newStart.plusMinutes(service.getDuration());

        // 3. check if barber is free

        List<Appointment> existingAppointments = appointmentRepository.findAll()
                .stream()
                .filter( a -> a.getBarber().getId().equals(barber.getId()))
                .collect(Collectors.toList());

        for (Appointment existing : existingAppointments){
            //we need to know when appointment its finishing
            LocalDateTime existingStart =existing.getStartTime();
            LocalDateTime existingEnd = existingStart.plusMinutes(existing.getServiceDetails().getDuration());

            if (newStart.isBefore(existingEnd) && existingStart.isBefore(newEnd)){
                log.warn("Conflict de programare pentru frizerul cu id  " + barber.getId());
                throw new RuntimeException("Intervalul orar este deja ocupat pentru acest frizer.");
            }
        }

        Appointment appointment =appointmentMapper.toEntity(appointmentDTO);

        appointment.setBarber(barber);
        appointment.setServiceDetails(service);

        Appointment savedAppointment = appointmentRepository.save(appointment);

        log.info("Programare facuta cu succes, ID: {}", savedAppointment.getId());

        return appointmentMapper.toDTO(savedAppointment);

    }




}
