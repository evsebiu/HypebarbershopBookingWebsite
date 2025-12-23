package com.hype.barbershop.Model.Mapper;

import com.hype.barbershop.Model.DTO.ServiceDetailsDTO;
import com.hype.barbershop.Model.Entity.ServiceDetails;
import org.springframework.stereotype.Component;

@Component
public class ServiceDetailsMapper {

    ServiceDetailsDTO toDTO(ServiceDetails serviceDetails){
        if (serviceDetails == null ) return null;

        ServiceDetailsDTO dto = new ServiceDetailsDTO();
        dto.setId(serviceDetails.getId());
        dto.setDuration(serviceDetails.getDuration());
        dto.setPrice(serviceDetails.getPrice());
        dto.setServiceName(serviceDetails.getServiceName());

        return dto;
    }

    ServiceDetails toEntity(ServiceDetailsDTO serviceDetailsDTO){
        if (serviceDetailsDTO == null) return null;

        ServiceDetails entity = new ServiceDetails();
        entity.setId(serviceDetailsDTO.getId());
        entity.setDuration(serviceDetailsDTO.getDuration());
        entity.setPrice(serviceDetailsDTO.getPrice());
        entity.setServiceName(serviceDetailsDTO.getServiceName());

        return entity;
    }
}
