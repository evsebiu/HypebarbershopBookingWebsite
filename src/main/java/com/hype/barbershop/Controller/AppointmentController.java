package com.hype.barbershop.Controller;

import com.hype.barbershop.Model.DTO.AppointmentDTO;
import com.hype.barbershop.Model.DTO.BarberDTO;
import com.hype.barbershop.Model.Entity.Appointment;
import com.hype.barbershop.Model.Entity.Barber;
import com.hype.barbershop.Model.Entity.ServiceDetails;
import com.hype.barbershop.Model.Mapper.BarberMapper;
import com.hype.barbershop.Service.AppointmentService;
import com.hype.barbershop.Service.BarberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/appointment") // Atenție: verifica URL-ul din HTML (singular sau plural)
@RequiredArgsConstructor
public class AppointmentController {

    private final BarberService barberService;
    private final AppointmentService appointmentService;
    private final BarberMapper barberMapper;

    // Nu ai nevoie de ServiceDetailsMapper aici, pentru că BarberDTO conține deja lista de Entități ServiceDetails

    @GetMapping("/new")
    public String showAppointmentForm(
            @RequestParam("barberId") Long barberId,
            @RequestParam("serviceId") Long serviceId,
            Model model
    ) {
        // 1. Găsim frizerul (care vine ca DTO din service)
        BarberDTO barberDTO = barberService.findById(barberId)
                .orElseThrow(() -> new RuntimeException("Frizer negăsit"));

        // 2. Convertim Barber DTO -> Entity
        // Avem nevoie de Entitate pentru a o seta pe obiectul Appointment
        Barber barberEntity = barberMapper.toEntity(barberDTO);

        // 3. Găsim serviciul
        // În BarberDTO-ul tău, lista este List<ServiceDetails> (deci sunt deja Entități!)
        ServiceDetails selectedService = barberDTO.getServiceDetails().stream()
                .filter(s -> s.getId().equals(serviceId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Serviciu negăsit"));

        // 4. Pregătim obiectul Appointment gol pentru formular
        Appointment appointment = new Appointment();

        // Aici setăm ENTITĂȚILE, nu DTO-urile. Acum funcționează corect.
        appointment.setBarber(barberEntity);
        appointment.setServiceDetails(selectedService);

        // 5. Trimitem datele în HTML
        model.addAttribute("appointment", appointment); // Obiectul care se va salva
        model.addAttribute("barber", barberDTO);        // Pentru afișare text (Nume, etc)
        model.addAttribute("service", selectedService); // Pentru afișare text (Preț, Nume)

        return "appointment_form";
    }

    // Am adăugat și metoda de salvare ca să fie controller-ul complet
    @PostMapping("/save")
    public String saveAppointment(@ModelAttribute AppointmentDTO appointment) {
        // Salvăm programarea și obținem obiectul salvat (care are acum ID generat)
        AppointmentDTO savedAppointment = appointmentService.createAppointment(appointment);
        // NOTĂ: Asigură-te că în Service ai o metodă care returnează entitatea salvată.
        // Dacă metoda ta din service este void, schimb-o să returneze Appointment.

        // Redirecționăm către pagina de confirmare cu ID-ul programării
        return "redirect:/appointment/confirmed/" + savedAppointment.getId();
    }

    @GetMapping("/confirmed/{id}")
    public String showConfirmationPage(@PathVariable Long id, Model model) {
        // Căutăm programarea după ID pentru a afișa detaliile
        // (Presupunând că ai o metodă findById în service sau repository)
        AppointmentDTO appointment = appointmentService.getById(id)
                .orElseThrow(() -> new RuntimeException("Programarea nu a fost găsită"));

        model.addAttribute("appointment", appointment);
        return "appointment_confirmed";
    }
}