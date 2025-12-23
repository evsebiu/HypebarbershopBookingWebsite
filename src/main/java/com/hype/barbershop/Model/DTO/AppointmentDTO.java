package com.hype.barbershop.Model.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {

    private Long id;
    @NotBlank(message = "Numele este necesar.")
    private String clientName;

    @NotBlank(message = "Numarul de telefon este necesar.")
    @Pattern(regexp = "^(\\+4|0)7[0-9]{8}$", message = "Formatul numărului de telefon " +
            "este invalid (ex: 0722123456 sau +40722123456)")
    private String phoneNumber;

    @Email
    @NotBlank(message = "E-mailul este necesar. Introdu unul valid.")
    private String clientEmail;

    @Future(message = "Programarea trebuie sa fie in viitor.")
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime startTime;


    private String additionalInfo;

    private Long barberId;
    private Long serviceId;
}
