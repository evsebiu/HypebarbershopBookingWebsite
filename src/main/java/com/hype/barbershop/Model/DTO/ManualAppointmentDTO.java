package com.hype.barbershop.Model.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ManualAppointmentDTO {

    @NotBlank(message = "Numele este necesar")
    private String clientName;


    private String phoneNumber;

    @NotNull(message = "Data este obligatorie")
    private LocalDateTime startTime;

    @NotNull
    private Long serviceId;

    private Long barberId;
}
