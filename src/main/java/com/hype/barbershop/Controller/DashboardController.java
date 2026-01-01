package com.hype.barbershop.Controller;


import com.hype.barbershop.Service.AppointmentService;
import com.hype.barbershop.Service.BarberService;
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

    private final AppointmentService appointmentService;
    private final BarberService barberService;

    @GetMapping
    public String viewDashboard(Authentication authentication, Model model){
        String email = authentication.getName();

        //common data for both roles : each own appointments
        model.addAttribute("myAppointments", appointmentService.getAllAppointmentsForCurrentBarber(email));

        // if it's admin, we create list of barbers for management
        boolean isAdmin = authentication.getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        if (isAdmin){
            model.addAttribute("allBarbers", barberService.getAll());
            model.addAttribute("isAdminView", true);
        } else {
            model.addAttribute("isAdminView", false);
        }

        // return path to html
        return "dashboard/index";
    }
}
