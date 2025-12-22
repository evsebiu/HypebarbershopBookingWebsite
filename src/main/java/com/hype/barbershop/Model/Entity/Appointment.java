package com.hype.barbershop.Model.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Numele este necesar.")
    @Column(name = "patient_name", nullable = false)
    private String clientName;

    @NotBlank(message = "Numarul de telefon este necesar.")
    @Column(name = "phone_number", nullable = false)
    @Pattern(regexp = "^(\\+4|0)7[0-9]{8}$", message = "Formatul numărului de telefon " +
            "este invalid (ex: 0722123456 sau +40722123456)")
    private String phoneNumber;

    @Email
    @NotNull(message = "E-mailul este necesar. Introdu unul valid.")
    @Column(name = "client_email", nullable = false)
    private String clientEmail;

    @Future(message = "Programarea trebuie sa fie in viitor.")
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    @Column(name = "local_date_time", nullable = false)
    private LocalDateTime localDateTime;

    @Column(name = "additional_info", nullable = false)
    private String additionalInfo;

}
