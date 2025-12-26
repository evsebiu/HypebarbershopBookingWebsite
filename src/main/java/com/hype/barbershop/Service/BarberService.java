package com.hype.barbershop.Service;


import com.hype.barbershop.Exceptions.DuplicateResourceException;
import com.hype.barbershop.Exceptions.IllegalArgumentException;
import com.hype.barbershop.Exceptions.RuntimeException;
import com.hype.barbershop.Model.DTO.BarberDTO;
import com.hype.barbershop.Model.Entity.Barber;
import com.hype.barbershop.Model.Mapper.BarberMapper;
import com.hype.barbershop.Repository.AppointmentRepository;
import com.hype.barbershop.Repository.BarberRepository;
import com.hype.barbershop.Repository.ServiceDetailsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j

public class BarberService {

    private final BarberMapper barberMapper;
    private final AppointmentRepository appointmentRepo;
    private final BarberRepository barberRepo;
    private final ServiceDetailsRepository serviceDetailsRepo;

    public BarberService (AppointmentRepository appointmentRepo,
                          BarberRepository barberRepo,
                          ServiceDetailsRepository serviceDetailsRepo,
                          BarberMapper barberMapper){
        this.appointmentRepo=appointmentRepo;
        this.barberRepo=barberRepo;
        this.serviceDetailsRepo=serviceDetailsRepo;
        this.barberMapper=barberMapper;
    }

    //GET methods

    @Transactional(readOnly = true)
    public List<BarberDTO> getIfActive() {
        log.debug("Se cauta frizerii activi..." );

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
    public List<BarberDTO> getByFirstName(String firstName){
        log.debug("Se solicita frizerii cu first name {} ", firstName);

        return barberRepo.findByFirstName(firstName)
                .stream()
                .map(barberMapper::toDTO)
                .collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public List<BarberDTO> getByLastName(String lastName){
        log.debug("Se solicita frizerii cu last name{}", lastName);

        return barberRepo.findByLastName(lastName)
                .stream()
                .map(barberMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public BarberDTO createBarber(BarberDTO barberDTO){

        // null exception
        if (barberDTO == null){
            throw new IllegalArgumentException("Detaliile frizerului sunt necesare.");
        }

        //check if barber email exists in database because it's unique

        if (barberDTO.getEmail() !=null && barberRepo.findByEmail(barberDTO.getEmail()).isPresent()){
            throw new DuplicateResourceException("Un frizer cu acest email exista deja in baza de date");
        }

        //save
        Barber barber = barberMapper.toEntity(barberDTO);
        Barber savedBarber = barberRepo.save(barber);

        return barberMapper.toDTO(savedBarber);
    }

    @Transactional
    public BarberDTO updateBarber (BarberDTO barberDTO)
}


