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
        dto.setStartTime(appointment.getStartTime());
        dto.setAdditionalInfo(appointment.getAdditionalInfo());

        //mapping ID from object relationships
        if (appointment.getBarber() != null){
            dto.setBarberId(appointment.getBarber().getId());
            dto.setBarberName(appointment.getBarber().getFirstName() + " " + appointment.getBarber().getLastName());
        }
        if (appointment.getServiceDetails() !=null){
            dto.setServiceId(appointment.getServiceDetails().getId());

            dto.setServiceName(appointment.getServiceDetails().getServiceName());
            dto.setPrice(appointment.getServiceDetails().getPrice());
            dto.setDuration(appointment.getServiceDetails().getDuration());
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
        appointment.setStartTime(dto.getStartTime());
        appointment.setAdditionalInfo(dto.getAdditionalInfo());

        return appointment;
    }
}
