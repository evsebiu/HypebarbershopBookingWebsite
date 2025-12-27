package com.hype.barbershop.Service;


import com.hype.barbershop.Exceptions.BarbershopDuplicateResource;
import com.hype.barbershop.Exceptions.IllegalBarbershopArgument;
import com.hype.barbershop.Exceptions.BarbershopResourceNotFound;
import com.hype.barbershop.Model.DTO.BarberDTO;
import com.hype.barbershop.Model.DTO.BarberRegistrationDTO;
import com.hype.barbershop.Model.Entity.Barber;
import com.hype.barbershop.Model.Enums.Role;
import com.hype.barbershop.Model.Mapper.BarberMapper;
import com.hype.barbershop.Repository.AppointmentRepository;
import com.hype.barbershop.Repository.BarberRepository;
import com.hype.barbershop.Repository.ServiceDetailsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public BarberDTO createBarber(BarberRegistrationDTO regDTO){
        // null exception
        if (regDTO == null){
            throw new IllegalBarbershopArgument("Detaliile frizerului sunt necesare.");
        }

        //check if barber email exists in database because it's unique

        if (regDTO.getEmail() !=null && barberRepo.findByEmail(regDTO.getEmail()).isPresent()){
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
        if (Boolean.TRUE.equals(regDTO.getIsAdmin())){
            barber.setRole(Role.ROLE_ADMIN);
        } else {
            barber.setRole(Role.ROLE_BARBER);
        }

        Barber saveBarber = barberRepo.save(barber);
        return barberMapper.toDTO(saveBarber);
    }

    @Transactional
    public BarberDTO updateBarber (Long id, BarberDTO barberDTO){

        // find existing barber
        Barber existingBarber = barberRepo.findById(id)
                .orElseThrow(()->  new BarbershopResourceNotFound("Frizerul cautat nu a fost gasit. ID: " + id ));

        //validate input

        if (barberDTO == null){
            throw new IllegalBarbershopArgument("Detaiile frizerului nu pot fi nulle");
        }

        // check if email is already taken by another barber

        if (!existingBarber.getEmail().equals(barberDTO.getEmail())){
            boolean emailExists = barberRepo.existsEmailAndIdNot(barberDTO.getEmail(), existingBarber.getId());
            if (emailExists){
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
    public void deleteBarber(Long id){
        if (id == null){
            throw new IllegalBarbershopArgument("ID-ul nu poate fi null");
        }

        Barber barberToDelete = barberRepo.findById(id)
                .orElseThrow(()-> new BarbershopResourceNotFound("ID-ul frizerului nu a fost gasit"));
        barberRepo.delete(barberToDelete);
    }
}


