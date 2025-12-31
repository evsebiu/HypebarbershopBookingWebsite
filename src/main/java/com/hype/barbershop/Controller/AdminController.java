package com.hype.barbershop.Controller;

import ch.qos.logback.core.model.Model;
import com.hype.barbershop.Service.AppointmentService;
import com.hype.barbershop.Service.BarberService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    // Servicii necesare pentru dashboard
    private final AppointmentService appointmentService;
    private final BarberService barberService;

    public AdminController(AppointmentService appointmentService, BarberService barberService) {
        this.appointmentService = appointmentService;
        this.barberService = barberService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Aici vei lua ID-ul frizerului logat din Security Context
        // model.addAttribute("appointments", appointmentService.findAllForToday());
        return "admin/dashboard";
    }
}
