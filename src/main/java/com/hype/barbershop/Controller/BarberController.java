package com.hype.barbershop.Controller;

import com.hype.barbershop.Model.DTO.BarberDTO;
import com.hype.barbershop.Model.DTO.ServiceDetailsDTO;
import com.hype.barbershop.Model.Entity.Barber;
import com.hype.barbershop.Model.Entity.ServiceDetails;
import com.hype.barbershop.Service.BarberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/barber")
@RequiredArgsConstructor

public class BarberController {

    private final BarberService barberService;

    @GetMapping("/{id}")
    public String getBarberPage(@PathVariable Long id, Model model) {

        Optional<BarberDTO> barberOpt = barberService.findById(id);

        if (barberOpt.isPresent()) {

            BarberDTO barberDTO = barberOpt.get();

            // add DTO
            model.addAttribute("barber", barberDTO);

            List<ServiceDetails> services = barberDTO.getServiceDetails();
            if (services == null) {
                services = new ArrayList<>();
            }

            // services

            model.addAttribute("services", services);

            return "barber";

        } else {
            return "redirect:/";
        }
    }
}

