package com.hype.barbershop.Controller;

import com.hype.barbershop.Exceptions.BarbershopException;
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
    public String saveAppointment(@Valid @ModelAttribute("appointment") AppointmentDTO appointmentDTO,
                                  BindingResult bindingResult,
                                  Model model) {
        // verify if exists validation errors
        if (bindingResult.hasErrors()){
            // reload barber and service details to rend page again
            // get DTO's id which came from form
            Long barberId = appointmentDTO.getBarberId();
            Long serviceId = appointmentDTO.getServiceId();

            //find barber (from method @GetMapping("/new))
            BarberDTO barberDTO = barberService.findById(barberId)
                    .orElseThrow(()-> new BarbershopException("Frizerul nu a fost gasit"));

            // find service
            ServiceDetails selectedService = barberDTO.getServiceDetails()
                    .stream()
                    .filter(s -> s.getId().equals(serviceId))
                    .findFirst()
                    .orElseThrow(()-> new BarbershopException("Serviciu inexistent"));

            // gather data back in model to avoid display erros in HTML
            model.addAttribute("barber", barberDTO);
            model.addAttribute("service", selectedService);

            //return form page, which now contains erorrs
            return"appointment_form";

        }

        // if there are no erros we continue save

        AppointmentDTO savedAppointment = appointmentService.createAppointment(appointmentDTO);
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