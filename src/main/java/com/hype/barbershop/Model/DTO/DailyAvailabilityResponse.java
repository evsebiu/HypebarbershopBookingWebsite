package com.hype.barbershop.Model.DTO;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
public class DailyAvailabilityResponse {

    private Boolean isAvailable;

    private String message;

    private List<String> availableSlots;

    private LocalDate nextAvailableDate;

    private String nextAvailableDayName;

}
