package com.hype.barbershop.Model.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDetailsDTO {

    private Long id;

    @NotBlank(message = "Serviciul ce urmeaza a fi adaugat are nevoie de un nume.")
    private String serviceName;

    @NotBlank(message = "Pretul este necesar pentru adaugarea serviciului.")
    private double price;

    @NotNull(message = "Serviciul adaugat are nevoie de o durata.")
    private int duration;
}
