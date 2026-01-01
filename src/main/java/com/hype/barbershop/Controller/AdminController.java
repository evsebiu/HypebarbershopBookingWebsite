package com.hype.barbershop.Controller;

import com.hype.barbershop.Service.AppointmentService;
import com.hype.barbershop.Service.BarberService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/dashboard")
public class AdminController {

    // Servicii necesare pentru dashboard
    private final AppointmentService appointmentService;
    private final BarberService barberService;

    public AdminController(AppointmentService appointmentService, BarberService barberService) {
        this.appointmentService = appointmentService;
        this.barberService = barberService;
    }

    @GetMapping
    public String dashboard(Authentication authentication, Model model) {
        // extract email of logged-in user

        String currentEmail = authentication.getName();

        model.addAttribute("myAppointments", appointmentService.getByEmail(currentEmail));

        // verify if user has admin role
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        if (isAdmin) {
            // admin can see all barbers so ge can manage them
            model.addAttribute("allBarbers", barberService.getIfActive());
            model.addAttribute("isAdminView", true);
        } else {
            model.addAttribute("isAdminView", false);
        }

        return "admin/dashboard";
    }
}
