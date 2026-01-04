package com.hype.barbershop.Model.Entity;

import com.hype.barbershop.Model.Enums.Role;
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


    //security fields
    @Column(name = "password" , nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    private Boolean isActive;

    @OneToMany(mappedBy = "barber", cascade = CascadeType.ALL)
    private List<Appointment> appointments;

    @OneToMany(mappedBy = "barber", fetch = FetchType.LAZY)
    private List<ServiceDetails> serviceDetails;

    @OneToMany(mappedBy = "barber", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BarberSchedule> weeklySchedule;

    @OneToMany(mappedBy = "barber", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BarberDayOff> daysOff;

}
