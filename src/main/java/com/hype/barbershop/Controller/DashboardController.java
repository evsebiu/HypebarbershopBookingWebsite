package com.hype.barbershop.Controller;


import com.hype.barbershop.Model.DTO.BarberDTO;
import com.hype.barbershop.Service.AppointmentService;
import com.hype.barbershop.Service.BarberService;
import com.hype.barbershop.Service.ServiceDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor

public class DashboardController {

    private final ServiceDetailsService serviceDetailsService;
    private final AppointmentService appointmentService;
    private final BarberService barberService;

    @GetMapping
    public String viewDashboard(Authentication authentication, Model model){
        // 1. Luăm email-ul (username-ul) utilizatorului logat
        String email = authentication.getName();

        // 2. Îl trimitem către HTML ca să îl putem afișa
        model.addAttribute("currentUser", email);

        // 3. Încărcăm programările proprii
        model.addAttribute("myAppointments", appointmentService.getAllAppointmentsForCurrentBarber(email));

        // 2. ADD THIS: Serviciile Mele (Trebuie să iei ID-ul frizerului mai întâi)
        // Poți folosi o metodă helper în Service sau repository
        // Presupunând că ai acces la BarberRepository sau ServiceService poate găsi după email:
        BarberDTO currentBarber = barberService.getByEmail(email).orElse(null);
        if(currentBarber != null) {
            model.addAttribute("myServices", serviceDetailsService.getByBarberId(currentBarber.getId()));
        }


        // 4. Verificăm rolul pentru Admin
        boolean isAdmin = authentication.getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        if (isAdmin){
            // Admin-ul vede lista completă
            model.addAttribute("allBarbers", barberService.getAll()); // Asigură-te că metoda din service se numește getIfActive() sau getAll()
            model.addAttribute("isAdminView", true);
        } else {
            model.addAttribute("isAdminView", false);
        }

        return "dashboard/index";
    }
}
