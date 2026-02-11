package com.hype.barbershop.Controller;

import com.hype.barbershop.Model.DTO.ServiceDetailsDTO;
import com.hype.barbershop.Service.ServiceDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Provider;
import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
@Slf4j
public class ServiceDetailsControllerAPI {

    private final ServiceDetailsService serviceDetailsService;

    // 1. GET ALL & FILTER
    // Permite: /api/services (toate) SAU /api/services?price=50 SAU /api/services?name=Tuns
    @GetMapping
    public ResponseEntity<List<ServiceDetailsDTO>> getServices(Authentication authentication) {

        String email = authentication.getName();
        return ResponseEntity.ok(serviceDetailsService.getServiceByBarberEmail(email));
    }

    // 2. CREATE
    @PostMapping
    public ResponseEntity<ServiceDetailsDTO> createService(@RequestBody @Valid ServiceDetailsDTO serviceDTO,
                                                           Authentication authentication) {

        String email = authentication.getName();

        return new ResponseEntity<>(serviceDetailsService.addServiceForBarber(email, serviceDTO), HttpStatus.CREATED);
    }

    // 3. UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<ServiceDetailsDTO> updateService(@PathVariable Long id, @RequestBody @Valid ServiceDetailsDTO serviceDTO) {
        return ResponseEntity.ok(serviceDetailsService.updateService(id, serviceDTO));
    }

    // 4. DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceDetailsService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}