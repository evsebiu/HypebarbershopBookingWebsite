package com.hype.barbershop.Controller;

import com.hype.barbershop.Exceptions.IllegalBarbershopArgument;
import com.hype.barbershop.Model.DTO.AppointmentDTO;
import com.hype.barbershop.Model.DTO.DailyAvailabilityResponse;
import com.hype.barbershop.Model.DTO.ManualAppointmentDTO;
import com.hype.barbershop.Model.Enums.AppointmentStatus;
import com.hype.barbershop.Service.AppointmentService;
import com.hype.barbershop.Service.BarberService;
import com.hype.barbershop.Service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Slf4j


public class AppointmentControllerAPI {

    private final AppointmentService appointmentService;
    private final EmailService emailService;
    private final BarberService barberService;



    // 2. GET ALL (Dashboard)
    @GetMapping
    public ResponseEntity<List<AppointmentDTO>> getAll() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    // 3. SEARCH (Admin Dashboard Search Bar)
    // URL: /api/appointments/search?phone=0722...
    @GetMapping("/search")
    public ResponseEntity<List<AppointmentDTO>> searchAppointments(
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String name) {

        if (phone != null) return ResponseEntity.ok(appointmentService.getByPhoneNumber(phone));
        if (email != null) return ResponseEntity.ok(appointmentService.getByEmail(email));
        if (name != null) return ResponseEntity.ok(appointmentService.getByClientName(name));

        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    // 4. GET ONE
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDTO> getById(@PathVariable Long id) {
        return appointmentService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 5. UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<AppointmentDTO> updateAppointment(@PathVariable Long id, @RequestBody @Valid AppointmentDTO dto) {
        return ResponseEntity.ok(appointmentService.updateAppointmentAPI(id, dto));
    }

    // 6. DELETE (Cancel)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/slots")
    public ResponseEntity<DailyAvailabilityResponse> getAvailableSlots(
            @RequestParam Long barberId,
            @RequestParam Long serviceId,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate date) {

        // Apelăm noua metodă inteligentă din Service care ne returnează DTO-ul complet
        return ResponseEntity.ok(appointmentService.getAvailableSlotsWithNextDayFallback(barberId, serviceId, date));
    }

    @GetMapping("/my-appointments/filter")
    public ResponseEntity<List<AppointmentDTO>> filterAppointment(@RequestParam AppointmentStatus status,
                                                                  Principal principal){

        if (principal == null){
            throw new IllegalBarbershopArgument("Acces nepermis. Trebuie sa te loghezi intai.");
        }

        String email = principal.getName();


        return ResponseEntity.ok(appointmentService.getAppointmentByStatus(email, status));
    }

    @PostMapping("/manual-booking")
    public ResponseEntity<AppointmentDTO> createManualAppointment(@RequestBody @Valid ManualAppointmentDTO manualAppointmentDTO,
                                                                  Principal principal){
        if (principal == null){
            throw new IllegalBarbershopArgument("Acces nepermis. Trebuie sa te loghezi intai");
        }

        String email = principal.getName();

        AppointmentDTO savedAppointment = appointmentService.createManualAppointment(manualAppointmentDTO, email);

        return new ResponseEntity<>(savedAppointment, HttpStatus.CREATED);
    }



    // EMAIL SENDER

    @PostMapping
    public ResponseEntity<AppointmentDTO> createAppointment(@RequestBody @Valid AppointmentDTO appointmentDTO) {

        // 1. Salvam programarea in baza de date PRIMA DATA
        // Este important sa preluam obiectul returnat, deoarece poate contine ID-ul nou generat
        // sau detalii adaugate de AppointmentService.
        AppointmentDTO savedAppointment = appointmentService.createAppointment(appointmentDTO);

        // 2. Generam sabloanele HTML folosind datele salvate
        String clientEmailBody = emailService.generateClientTemplate(savedAppointment);
        String barberEmailBody = emailService.generateBarberTemplate(savedAppointment);

        // 3. Trimitem emailurile
        emailService.sendHtmlEmail(
                savedAppointment.getClientEmail(),
                "Confirmare Programare - Hype Barbershop",
                clientEmailBody
        );

       // find barber email
        String barberEmail = barberService.findById(savedAppointment.getBarberId())
                .orElseThrow(()-> new RuntimeException("Frizerul nu a fost gasit"))
                .getEmail();

        // send email to barber
        emailService.sendHtmlEmail(
                barberEmail,
                "Programare Noua: " + savedAppointment.getClientName(),
                barberEmailBody
        );

        // 4. Returnam raspunsul JSON catre React
        return new ResponseEntity<>(savedAppointment, HttpStatus.CREATED);
    }

}
