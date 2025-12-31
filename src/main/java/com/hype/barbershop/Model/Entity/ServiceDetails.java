package com.hype.barbershop.Model.Entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "services")
public class ServiceDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Serviciul trebuie sa contina un nume.")
    @Column(name = "service_name")
    private String serviceName;

    @NotNull(message = "Serviciul trebuie sa contina un pret")
    @Column(name = "price")
    private Double price;

    @NotNull(message = "Durata trebuie sa fie introdusa pentru serviciu")
    @Column(name = "duration")
    private Integer duration;

    @OneToMany(mappedBy = "serviceDetails")
    private List<Appointment> appointments;

    @ManyToOne
    @JoinColumn(name = "barber_id")
    private Barber barber;

}
