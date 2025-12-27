package com.hype.barbershop.Controller;

import com.hype.barbershop.Exceptions.IllegalBarbershopArgument;
import com.hype.barbershop.Model.DTO.BarberDTO;
import com.hype.barbershop.Model.DTO.BarberRegistrationDTO;
import com.hype.barbershop.Service.BarberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/barbers")
@RequiredArgsConstructor
@Slf4j
public class BarberController {

    private final BarberService barberService;

    // --- NON-CRUD METHODS (Business Logic) ---

    // 1. GET Activi (Pentru clienți - să vadă la cine se pot programa)
    // URL: /api/barbers/active
    @GetMapping("/active")
    public ResponseEntity<List<BarberDTO>> getActiveBarbers() {
        return ResponseEntity.ok(barberService.getIfActive());
    }

    // 2. SEARCH (Complex) - Caută după diverse criterii
    // URL: /api/barbers/search?email=... SAU /api/barbers/search?firstName=...
    @GetMapping("/search")
    public ResponseEntity<?> searchBarbers(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName) {

        if (email != null) {
            return barberService.getByEmail(email)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
        if (firstName != null) {
            return ResponseEntity.ok(barberService.getByFirstName(firstName));
        }
        if (lastName != null) {
            return ResponseEntity.ok(barberService.getByLastName(lastName));
        }
        return ResponseEntity.ok(barberService.getIfActive()); // Default
    }

    // --- CRUD METHODS ---

    // 3. CREATE (Register) - Admin Only
    @PostMapping("/register")
    public ResponseEntity<BarberDTO> createBarber(@RequestBody @Valid BarberRegistrationDTO regDTO) {
        log.info("REST request creare frizer: {}", regDTO.getEmail());
        return new ResponseEntity<>(barberService.createBarber(regDTO), HttpStatus.CREATED);
    }

    // 4. UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<BarberDTO> updateBarber(@PathVariable Long id, @RequestBody @Valid BarberDTO barberDTO) {
        return ResponseEntity.ok(barberService.updateBarber(id, barberDTO));
    }

    // 5. DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBarber(@PathVariable Long id) {
        barberService.deleteBarber(id);
        return ResponseEntity.noContent().build();
    }
}