package com.hype.barbershop.Model.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hype.barbershop.Model.Enums.AppointmentStatus;
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
@Table(name = "appointments")

public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Numele este necesar.")
    @Column(name = "client_name", nullable = false)
    private String clientName;

    @NotBlank(message = "Numarul de telefon este necesar.")
    @Column(name = "phone_number", nullable = false)
    @Pattern(regexp = "^(\\+4|0)7[0-9]{8}$", message = "Formatul numărului de telefon " +
            "este invalid (ex: 0722123456 sau +40722123456)")
    private String phoneNumber;

    @Email
    @NotBlank(message = "E-mailul este necesar. Introdu unul valid.")
    @Column(name = "client_email", nullable = false)
    private String clientEmail;

    @Future(message = "Programarea trebuie sa fie in viitor.")
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "additional_info", nullable = true)
    private String additionalInfo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AppointmentStatus status = AppointmentStatus.PENDING;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "barber_id", nullable = false)
    private Barber barber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceDetails serviceDetails;


}
