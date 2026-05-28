package com.hype.barbershop.Controller;

import com.hype.barbershop.Exceptions.BarbershopException;
import com.hype.barbershop.Model.DTO.BarberDayOffDTO;
import com.hype.barbershop.Service.BarberDayOffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/barbers/day-off")
@RequiredArgsConstructor
@Slf4j

public class BarberDayOffControllerAPI {

    private final BarberDayOffService barberDayOffService;


    @PostMapping
    public ResponseEntity<BarberDayOffDTO> addDayOff(@Valid @RequestBody BarberDayOffDTO dayOffDTO,
                                                     Principal principal){
        if (principal == null){
            throw new BarbershopException("Acces interzis! Trebuie sa fii autentificat");

        }

        String email = principal.getName();
        log.info("Frizerul {} încearcă să adauge o zi liberă pentru data de {}", email, dayOffDTO.getDate());

        BarberDayOffDTO savedDTO = barberDayOffService.addDayOff(dayOffDTO, email);
        return new ResponseEntity<>(savedDTO, HttpStatus.CREATED);
    }


    @GetMapping("/future")
    public ResponseEntity<List<BarberDayOffDTO>> getFutureDaysOff(@Valid @RequestBody BarberDayOffDTO dayOffDTO,
                                                                  Principal principal){

        if (principal == null){
            throw new BarbershopException("Acces interzis ! Trebuie sa fii autentificat!");
        }

        String email = principal.getName();
        return ResponseEntity.ok(barberDayOffService.getFutureDaysOff(email));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDayOff(@PathVariable("id") Long dayOffId, Principal principal) {
        if (principal == null) {
            throw new BarbershopException("Acces interzis! Trebuie să fii autentificat.");
        }

        String email = principal.getName();
        log.info("Frizerul {} încearcă să șteargă ziua liberă cu ID-ul {}", email, dayOffId);

        barberDayOffService.delteDayOff(dayOffId, email); // Folosim exact numele metodei tale
        return ResponseEntity.noContent().build();
    }

}
