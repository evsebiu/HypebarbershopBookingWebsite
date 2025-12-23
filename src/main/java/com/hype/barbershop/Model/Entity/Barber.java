package com.hype.barbershop.Model.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "barber")

public class Barber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name este obligatoriu")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Last name obligatoriu")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank(message = "Emailul este necesar.")
    @Email
    @Column(name = "email", nullable = false)
    private String email;

    private Boolean isActive;

    @OneToMany(mappedBy = "barber", cascade = CascadeType.ALL)
    private List<Appointment> appointments;

}
