package com.hype.barbershop.Controller;


import com.hype.barbershop.Service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController // important :  restcontroller returns data not html
@RequestMapping("/api/appointments")
@RequiredArgsConstructor

public class AvailabilityController {

    private final AppointmentService appointmentService;

    @GetMapping("/slots")
    public List<String> getSlots(
            @RequestParam Long barberId,
            @RequestParam Long serviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){

        return appointmentService.getAvailableSlots(barberId, serviceId, date);
    }
}
