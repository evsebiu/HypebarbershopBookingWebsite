package com.hype.barbershop.Controller;

import com.hype.barbershop.Model.DTO.ServiceDetailsDTO;
import com.hype.barbershop.Service.ServiceDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard/services")
@RequiredArgsConstructor
public class ServiceWebController {

    private final ServiceDetailsService serviceDetailsService;

    @PostMapping("/add")
    public String addService(ServiceDetailsDTO serviceDTO, Authentication authentication) {
        // Luăm email-ul celui logat
        String email = authentication.getName();

        // Apelăm serviciul creat la Pasul 1
        serviceDetailsService.addServiceForBarber(email, serviceDTO);

        return "redirect:/dashboard";
    }

    @PostMapping("/delete/{id}")
    public String deleteService(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();

        // Ștergem serviciul cu verificare de proprietar
        serviceDetailsService.deleteServiceForBarber(id, email);

        return "redirect:/dashboard";
    }
}