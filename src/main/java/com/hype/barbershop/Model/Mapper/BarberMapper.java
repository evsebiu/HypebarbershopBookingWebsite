package com.hype.barbershop.Model.Mapper;

import com.hype.barbershop.Model.DTO.BarberDTO;
import com.hype.barbershop.Model.DTO.ServiceDetailsDTO;
import com.hype.barbershop.Model.Entity.Barber;
import com.hype.barbershop.Model.Entity.ServiceDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class BarberMapper {

    private ServiceDetailsDTO toServiceDto(ServiceDetails entity) {
        if (entity == null) return null;
        ServiceDetailsDTO dto = new ServiceDetailsDTO();
        dto.setId(entity.getId());
        dto.setServiceName(entity.getServiceName());
        dto.setPrice(entity.getPrice());
        dto.setDuration(entity.getDuration());
        return dto;
    }

    public BarberDTO toDTO(Barber barber){
        if (barber == null) return null;

        BarberDTO dto = new BarberDTO();
        dto.setId(barber.getId());
        dto.setIsActive(barber.getIsActive());
        dto.setEmail(barber.getEmail());
        dto.setLastName(barber.getLastName());
        dto.setFirstName(barber.getFirstName());
        dto.setRole(barber.getRole());

        // Caută linia unde setezi serviceDetails și înlocuiește-o cu asta:
        if (barber.getServiceDetails() != null) {
            dto.setServiceDetails(barber.getServiceDetails().stream()
                    .map(this::toServiceDto) // Folosim metoda de mai sus
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public Barber toEntity(BarberDTO barberDTO){
        if (barberDTO == null) return null;

        Barber barber = new Barber();

        barber.setIsActive(barberDTO.getIsActive());
        barber.setEmail(barberDTO.getEmail());
        barber.setFirstName(barberDTO.getFirstName());
        barber.setLastName(barberDTO.getLastName());
        barber.setId(barberDTO.getId());
        barber.setRole(barberDTO.getRole());
        return barber;
    }
}
