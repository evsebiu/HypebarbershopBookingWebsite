package com.hype.barbershop.Model.Mapper;

import com.hype.barbershop.Model.DTO.AppointmentDTO;
import com.hype.barbershop.Model.Entity.Appointment;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public AppointmentDTO toDTO(Appointment appointment){

        if (appointment == null) return null;
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(appointment.getId());
        dto.setClientName(appointment.getClientName());
        dto.setPhoneNumber(appointment.getPhoneNumber());
        dto.setClientEmail(appointment.getClientEmail());
        dto.setLocalDateTime(appointment.getLocalDateTime());
        dto.setAdditionalInfo(appointment.getAdditionalInfo());

        //mapping ID from object relationships
        if (appointment.getBarber() != null){
            dto.setBarberId(appointment.getBarber().getId());
        }
        if (appointment.getServiceDetails() !=null){
            dto.setServiceId(appointment.getServiceDetails().getId());
        }

        return dto;
    }

    public Appointment toEntity(AppointmentDTO dto){
        if (dto == null) return null;

        Appointment appointment = new Appointment();
        appointment.setId(dto.getId());
        appointment.setClientEmail(dto.getClientEmail());
        appointment.setPhoneNumber(dto.getPhoneNumber());
        appointment.setClientName(dto.getClientName());
        appointment.setLocalDateTime(dto.getLocalDateTime());
        appointment.setAdditionalInfo(dto.getAdditionalInfo());

        return appointment;
    }
}
