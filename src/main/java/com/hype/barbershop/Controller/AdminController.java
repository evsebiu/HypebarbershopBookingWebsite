package com.hype.barbershop.Controller;

import com.hype.barbershop.Service.AppointmentService;
import com.hype.barbershop.Service.BarberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {

    // Servicii necesare pentru dashboard
    private final AppointmentService appointmentService;
    private final BarberService barberService;

    public AdminController(AppointmentService appointmentService, BarberService barberService) {
        this.appointmentService = appointmentService;
        this.barberService = barberService;
    }

    @PatchMapping("/barbers/toggle/{id}")
    public ResponseEntity<?> toggleBarberStatus(@PathVariable Long id) {
        // Aici vei apela serviciul care schimbă statusul (îl vom face imediat)

        barberService.toggleBarberStatus(id);

        return ResponseEntity.ok(Map.of("message", "Statusul frizerlui a fost actualizat"));
        // După acțiune, ne întoarcem pe dashboard-ul unificat
    }
    // Exemplu: Butonul de ștergere
    @PatchMapping("/barbers/delete/{id}")
    public ResponseEntity<?> deleteBarber(@PathVariable Long id) {
        barberService.deleteBarber(id);
        return ResponseEntity.ok(Map.of("message", "Frizerul a fost sters!"));
    }
}
