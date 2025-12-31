package com.hype.barbershop.Controller;

import com.hype.barbershop.Model.DTO.BarberDTO;
import com.hype.barbershop.Model.Enums.Role;
import com.hype.barbershop.Service.BarberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    private final BarberService barberService;


    // 1. Landing Page (Include descriere, frizeri, servicii)
    @GetMapping
    public String home(Model model) {
        // Luăm toți utilizatorii activi
        List<BarberDTO> activeUsers = barberService.getIfActive();

        // Filtrăm lista: păstrăm DOAR pe cei care au rolul ROLE_BARBER
        List<BarberDTO> onlyBarbers = activeUsers.stream()
                .filter(b -> b.getRole() == Role.ROLE_BARBER)
                .collect(Collectors.toList());

        // Trimitem lista filtrată către HTML
        model.addAttribute("barbers", onlyBarbers);

        return "index";
    }

    // 2. Pagini Statice / Legale
    @GetMapping("/gdpr.html")
    public String gdprPage() {
        return "legal/gdpr.html"; // Creezi un folder templates/legal/gdpr.html.html
    }

    @GetMapping("/terms")
    public String termsPage() {
        return "legal/terms";
    }

    @GetMapping("/cookies")
    public String cookiesPage() {
        return "legal/cookies";
    }
}