package com.hype.barbershop.Controller;

import com.hype.barbershop.Service.BarberService;
import com.hype.barbershop.Service.ServiceDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class PublicController {

    private final BarberService barberService;
    private final ServiceDetailsService serviceDetailsService;

    @GetMapping
    public String landingPage(Model model) {
        model.addAttribute("welcomeMessage", "Bine ai venit la Hype Barbershop! Stilul tău, prioritatea noastră.");

        // Get active barbers
        var barbers = barberService.getIfActive();
        model.addAttribute("barbers", barbers);

        return "index";
    }

    @GetMapping("/booking")
    public String bookingPage(@RequestParam(required = false) Long barberId, Model model) {
        if (barberId == null) {
            return "redirect:/#barbers";
        }

        // Get barber - you'll need to implement getById in BarberService
        // For now, let's get from the list
        var barbers = barberService.getIfActive();
        var selectedBarber = barbers.stream()
                .filter(b -> b.getId().equals(barberId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Frizerul nu a fost găsit"));

        model.addAttribute("barber", selectedBarber);

        // Get services - you'll need to implement getByBarberId
        // For now, use all services
        model.addAttribute("services", serviceDetailsService.getAllServices());

        return "booking";
    }
}