package com.hype.barbershop.Controller;

import com.hype.barbershop.Exceptions.BarbershopException;
import com.hype.barbershop.Model.DTO.AppointmentDTO;
import com.hype.barbershop.Model.DTO.BarberDTO;
import com.hype.barbershop.Model.DTO.ServiceDetailsDTO;
import com.hype.barbershop.Model.Entity.Appointment;
import com.hype.barbershop.Model.Entity.Barber;
import com.hype.barbershop.Model.Entity.ServiceDetails;
import com.hype.barbershop.Model.Mapper.BarberMapper;
import com.hype.barbershop.Service.AppointmentService;
import com.hype.barbershop.Service.BarberService;
import com.hype.barbershop.Service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;


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
            Model model) {

        // 1. Găsim BarberDTO (nu mai convertim la Entitate!)
        BarberDTO barberDTO = barberService.findById(barberId)
                .orElseThrow(() -> new RuntimeException("Frizer negăsit"));

        // 2. Găsim ServiceDetailsDTO din lista din BarberDTO
        ServiceDetailsDTO selectedService = barberDTO.getServiceDetails().stream()
                .filter(s -> s.getId().equals(serviceId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Serviciu negăsit"));

        // 3. Pregătim un AppointmentDTO gol pentru formular
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setBarberId(barberId);
        appointmentDTO.setServiceId(serviceId);
        appointmentDTO.setBarberName(barberDTO.getFirstName());
        appointmentDTO.setServiceName(selectedService.getServiceName());
        appointmentDTO.setPrice(selectedService.getPrice());

        // 4. Trimitem doar DTO-uri către HTML
        model.addAttribute("appointment", appointmentDTO);
        model.addAttribute("barber", barberDTO);
        model.addAttribute("service", selectedService);

        return "appointment_form";
    }

    // Am adăugat și metoda de salvare ca să fie controller-ul complet
    @PostMapping("/save")
    public String saveAppointment(@Valid @ModelAttribute("appointment") AppointmentDTO appointmentDTO,
                                  BindingResult bindingResult,
                                  Model model) {
        if (bindingResult.hasErrors()){
            // Re-încărcăm datele necesare pentru afișarea erorilor folosind ID-urile din DTO
            BarberDTO barberDTO = barberService.findById(appointmentDTO.getBarberId()).get();
            ServiceDetailsDTO serviceDTO = barberDTO.getServiceDetails().stream()
                    .filter(s -> s.getId().equals(appointmentDTO.getServiceId())).findFirst().get();

            model.addAttribute("barber", barberDTO);
            model.addAttribute("service", serviceDTO);
            return "appointment_form";
        }

        appointmentService.createAppointment(appointmentDTO);
        return "redirect:/"; // Sau pagina de confirmare
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