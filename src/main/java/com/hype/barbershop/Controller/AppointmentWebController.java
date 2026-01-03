package com.hype.barbershop.Controller;


import com.hype.barbershop.Exceptions.BarbershopException;
import com.hype.barbershop.Model.DTO.AppointmentDTO;
import com.hype.barbershop.Model.Entity.Barber;
import com.hype.barbershop.Service.AppointmentService;
import com.hype.barbershop.Service.ServiceDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/dashboard/appointment")
@RequiredArgsConstructor

public class AppointmentWebController {

    private final AppointmentService appointmentService;
    private final ServiceDetailsService serviceDetailsService;

    // editing form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, Authentication authentication){

        // get current logged-in user data for security verification
        String currentEmail = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        // LOAD DTO
        AppointmentDTO appointmentDTO = appointmentService.getAppointmentForEdit(id, currentEmail, isAdmin);

        model.addAttribute("appointment", appointmentDTO);

        // send list of services for dropdown
        // if barber can add/modify his services, we load only his services
        model.addAttribute("availableServices", serviceDetailsService.getAllServices());

        return "dashboard/edit_appointment";
    }

    @PostMapping("/update/{id}")
    public String updateAppointment(@PathVariable Long id,
                                    @Valid @ModelAttribute("appointment") AppointmentDTO appointmentDTO,
                                    BindingResult result,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes,
                                    Model model){
        if (result.hasErrors()){
            // if there are validations errors (e.g empty name we reload page with errors)
            model.addAttribute("availableServices", serviceDetailsService.getAllServices());
            return "dashboard/edit_appointment";
        } try{
            String currentEmail = authentication.getName();

            boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

            appointmentService.updateAppointment(id, appointmentDTO, currentEmail, isAdmin);

            redirectAttributes.addFlashAttribute("successMessage", "Programarea a fost modificata");

            return "redirect:/dashboard";

        } catch (BarbershopException e){
            // if we have a logic error, e.g overlappoing we stay in page and show message
            model.addAttribute("errorMessage", e.getMessage());
            // reload services
            model.addAttribute("availableServices", serviceDetailsService.getAllServices());
            return "dashboard/edit_appointment";

        }
    }

    @PostMapping("/delete/{id}")
    public String deleteAppointment (@PathVariable Long id,
                                     Authentication authentication,
                                     RedirectAttributes redirectAttributes) {

        try {
            // verify permissions
            String currentEmail = authentication.getName();
            boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

            appointmentService.getAppointmentForEdit(id, currentEmail, isAdmin);

            appointmentService.deleteAppointment(id);

            redirectAttributes.addFlashAttribute("successMessage", "Programarea a fost anulata");
        } catch (BarbershopException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/dashboard";
    }
}
