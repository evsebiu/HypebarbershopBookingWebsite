package com.hype.barbershop.Model.Entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class ServiceDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Serviciul trebuie sa contina un nume.")
    @Column(name = "service_name")
    private String serviceName;

    @NotBlank(message = "Serviciul trebuie sa contina un pret")
    @Column(name = "price")
    private double price;

    @NotNull(message = "Durata trebuie sa fie introdusa pentru serviciu")
    @Column(name = "duration")
    private int duration;


}
