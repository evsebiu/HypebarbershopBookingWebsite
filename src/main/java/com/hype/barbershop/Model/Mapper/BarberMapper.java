package com.hype.barbershop.Model.Mapper;

import com.hype.barbershop.Model.DTO.BarberDTO;
import com.hype.barbershop.Model.Entity.Barber;
import org.springframework.stereotype.Component;

@Component
public class BarberMapper {

    public BarberDTO toDTO(Barber barber){
        if (barber == null) return null;

        BarberDTO dto = new BarberDTO();
        dto.setId(barber.getId());
        dto.setIsActive(barber.getIsActive());
        dto.setEmail(barber.getEmail());
        dto.setLastName(barber.getLastName());
        dto.setFirstName(barber.getFirstName());
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
        return barber;
    }
}
