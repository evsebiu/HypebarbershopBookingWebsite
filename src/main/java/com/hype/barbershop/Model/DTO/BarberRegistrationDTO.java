package com.hype.barbershop.Model.DTO;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BarberRegistrationDTO {

    @NotBlank(message = "First name este obligatoriu")
    private String firstName;

    @NotBlank(message = "Last name este obligatoriu")
    private String lastName;

    @Email
    @NotBlank(message = "Emailul este obligatoriu")
    private String email;

    private String rawPassword;

    private Boolean isAdmin;


}
