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

        // show barbers active
        List<BarberDTO> onlyBarbers = activeUsers;

        // Trimitem lista filtrată către HTML
        model.addAttribute("barbers", onlyBarbers);

        return "index";
    }

    // 2. Pagini Statice / Legale
    @GetMapping("/gdpr")
    public String gdprPage() {
        return "legal/gdpr";
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