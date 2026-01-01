package com.hype.barbershop.Controller;

import com.hype.barbershop.Service.AppointmentService;
import com.hype.barbershop.Service.BarberService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    @PostMapping("/barbers/toggle/{id}")
    public String toggleBarberStatus(@PathVariable Long id) {
        // Aici vei apela serviciul care schimbă statusul (îl vom face imediat)
        barberService.toggleBarberStatus(id);
        return "redirect:/dashboard"; // După acțiune, ne întoarcem pe dashboard-ul unificat
    }
    // Exemplu: Butonul de ștergere
    @PostMapping("/barbers/delete/{id}")
    public String deleteBarber(@PathVariable Long id) {
        barberService.deleteBarber(id);
        return "redirect:/dashboard";
    }
}
