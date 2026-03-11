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

    @NotBlank(message = "Numarul de telefon este necesar")
    @Pattern(regexp = "^(\\+4|0)7[0-9]{8}$", message = "Formatul numărului de telefon " +
            "este invalid (ex: 0722123456 sau +40722123456)")
    private String phoneNumber;

    @NotNull(message = "Data este obligatorie")
    private LocalDateTime startTime;

    @NotNull
    private Long serviceId;

    private Long barberId;
}
