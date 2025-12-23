package com.hype.barbershop.Model.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BarberDTO {

    private Long id;

    @NotBlank(message = "First name este obligatoriu")
    private String firstName;

    @NotBlank(message = "Last name este obligatoriu")
    private String lastName;

    @Email
    @NotBlank(message = "Emailul este necesar.")
    private String email;

    private Boolean isActive;


}
