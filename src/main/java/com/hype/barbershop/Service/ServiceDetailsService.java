package com.hype.barbershop.Service;


import com.hype.barbershop.Exceptions.BarbershopException;
import com.hype.barbershop.Exceptions.BarbershopResourceNotFound;
import com.hype.barbershop.Model.DTO.ServiceDetailsDTO;
import com.hype.barbershop.Model.Mapper.ServiceDetailsMapper;
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


}
