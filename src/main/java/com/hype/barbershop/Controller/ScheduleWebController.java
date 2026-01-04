package com.hype.barbershop.Controller;

import com.hype.barbershop.Model.DTO.WeeklyScheduleDTO;
import com.hype.barbershop.Service.BarberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/dashboard/schedule")
@RequiredArgsConstructor
public class ScheduleWebController {

    private final BarberService barberService;

    @GetMapping
    public String showScheduleForm(Model model, Authentication authentication) {
        String email = authentication.getName();
        WeeklyScheduleDTO schedule = barberService.getBarberSchedule(email);

        model.addAttribute("scheduleForm", schedule);
        return "dashboard/schedule_form"; // Vom crea acest HTML
    }

    @PostMapping("/save")
    public String saveSchedule(@ModelAttribute WeeklyScheduleDTO scheduleForm,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        String email = authentication.getName();
        barberService.updateSchedule(email, scheduleForm);

        redirectAttributes.addFlashAttribute("successMessage", "Programul a fost actualizat!");
        return "redirect:/dashboard/schedule";
    }
}