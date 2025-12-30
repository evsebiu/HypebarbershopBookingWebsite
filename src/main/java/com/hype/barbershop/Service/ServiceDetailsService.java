package com.hype.barbershop.Service;


import com.hype.barbershop.Exceptions.BarbershopException;
import com.hype.barbershop.Exceptions.BarbershopResourceNotFound;
import com.hype.barbershop.Exceptions.IllegalBarbershopArgument;
import com.hype.barbershop.Model.DTO.ServiceDetailsDTO;
import com.hype.barbershop.Model.Entity.Barber;
import com.hype.barbershop.Model.Entity.ServiceDetails;
import com.hype.barbershop.Model.Mapper.ServiceDetailsMapper;
import com.hype.barbershop.Repository.BarberRepository;
import com.hype.barbershop.Repository.ServiceDetailsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor

public class ServiceDetailsService {

    private final ServiceDetailsRepository serviceDetailsRepo;
    private final ServiceDetailsMapper serviceDetailsMapper;

    // GET methods

    @Transactional(readOnly = true)
    public List<ServiceDetailsDTO> getByServiceName(String serviceName){

        log.debug("Se solicita serviciul cu numele... {} ", serviceName);

        if (serviceName == null || serviceName.trim().isEmpty()){
            log.warn("Serviciul solicitat nu a fost gasit ", serviceName);
            throw new BarbershopException("Numele este necesar.");

        }

        List<ServiceDetailsDTO> service = serviceDetailsRepo.findByServiceNameContainingIgnoreCase(serviceName)
                .stream()
                .map(serviceDetailsMapper::toDTO)
                .collect(Collectors.toList());
        log.info("S-a returnat serviciul solicitat {} ", serviceName);

        return service;
    }

    @Transactional(readOnly = true)
    public List<ServiceDetailsDTO> getByPrice(Double price){

        log.debug("Se solicita serviciul cu pretul... {} ", price);

        if (price == null){
            log.warn("Serviciul cu pretul {} este null ", price);
            throw new BarbershopException("Pretul serviciului solicitat nu poate fi null.");
        }

        List<ServiceDetailsDTO> service = serviceDetailsRepo.findByPrice(price)
                .stream()
                .map(serviceDetailsMapper::toDTO)
                .collect(Collectors.toList());

        log.info("S-a returnat serviciul/serviciile cu pretul {} ", price);

        return service;

    }

    @Transactional(readOnly = true)
    public List<ServiceDetailsDTO> getByDuration(Integer duration){

        log.debug("Se solicita serviciul/serviciile cu durata {} ", duration);

        if (duration == null){
            log.warn("Serviciul solicitat este null");
           throw new BarbershopException("Serviciul cu durata solicitata nu poate fi null.");
        }

        List<ServiceDetailsDTO> service =  serviceDetailsRepo.findByDuration(duration)
                .stream()
                .map(serviceDetailsMapper::toDTO)
                .collect(Collectors.toList());

        log.info("S-a returnat serviciul/serviciile solicitate cu durata {} ", duration);

        return service;
    }


    @Transactional
    public ServiceDetailsDTO createService (ServiceDetailsDTO serviceDetailsDTO){
        log.debug("Se creaza serviciul...");

        //null validation
        if (serviceDetailsDTO == null){
            throw new IllegalBarbershopArgument("Detaliile serviciului sunt necesare.");
        }

        // check if barber exists

        var existingService  =  serviceDetailsRepo.findByServiceNameContainingIgnoreCase(serviceDetailsDTO.getServiceName());

        if (!existingService.isEmpty()){

            for (ServiceDetails s : existingService){
                if (s.getServiceName().equalsIgnoreCase(serviceDetailsDTO.getServiceName()))
                throw new IllegalBarbershopArgument("Serviciul cu acest nume exista deja in baza de date.");
            }
        }

        // data validations (optional but useful)

        if (serviceDetailsDTO.getPrice() < 0 ){
            throw new IllegalBarbershopArgument("Pretul trebuie sa fie unul pozitiv");
        }

        if (serviceDetailsDTO.getDuration() < 0 ){
            throw new IllegalBarbershopArgument("Durata serviciului trebuie sa fie pozitiva");
        }

        // save

        ServiceDetails serviceDetails = serviceDetailsMapper.toEntity(serviceDetailsDTO);
        ServiceDetails savedService = serviceDetailsRepo.save(serviceDetails);

        log.info("Serviciu creat cu succes, ID {} ", savedService.getId());

        return serviceDetailsMapper.toDTO(savedService);

    }


    public ServiceDetailsDTO updateService (Long id, ServiceDetailsDTO serviceDetailsDTO){

        log.info("Incercare de actualizare a serviciului cu ID {} ", id);

        if (serviceDetailsDTO == null){
            throw new IllegalBarbershopArgument("Detaliile serviciului sunt necesare pentru actualizare.");
        }

        // check if service exists

        ServiceDetails existingService  = serviceDetailsRepo.findById(id)
                .orElseThrow(()-> new BarbershopException("Serviciul solicitat nu exista."));

        // check if name is already taken

        if (!existingService.getServiceName().equals(serviceDetailsDTO.getServiceName())){
            boolean nameExists = serviceDetailsRepo.existsByServiceNameAndIdNot(serviceDetailsDTO.getServiceName(), existingService.getId());
            if (nameExists){
                throw new IllegalBarbershopArgument("Numele serviciului este deja activ pe website /  database");
            }
        }

        //update fields
        existingService.setServiceName(serviceDetailsDTO.getServiceName());
        existingService.setDuration(serviceDetailsDTO.getDuration());
        existingService.setPrice(serviceDetailsDTO.getPrice());


        //save
        ServiceDetails serviceDetails = serviceDetailsMapper.toEntity(serviceDetailsDTO);
        ServiceDetails savedService = serviceDetailsRepo.save(serviceDetails);

        log.info("S-a actualizat cu succes serviciul solicitat cu ID {} ", savedService.getId());

        return serviceDetailsMapper.toDTO(savedService);
    }

    public void deleteService(Long id){
        log.info("Se solicita stergerea serviciului cu ID {} ", id);
        if (id == null ){
            throw new IllegalBarbershopArgument("ID-ul serviciului nu poate fi null");
        }

        ServiceDetails serviceToDelete  = serviceDetailsRepo.findById(id)
                .orElseThrow(()-> new BarbershopResourceNotFound("Serviciul solicitat nu exista"));

        serviceDetailsRepo.delete(serviceToDelete);

        log.info("S-a sters cu succes serviciul cu ID {} ", serviceToDelete.getId());
    }

}
