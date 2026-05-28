package com.hype.barbershop.Model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@RequiredArgsConstructor

public class BarberDayOffDTO {

    private Long id;

    private Long barberId;

    private LocalDate date;

    private String reason;

}
