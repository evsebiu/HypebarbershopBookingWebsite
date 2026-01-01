package com.hype.barbershop.Controller;

import com.hype.barbershop.Model.DTO.ServiceDetailsDTO;
import com.hype.barbershop.Service.ServiceDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<ServiceDetailsDTO>> getServices(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double price,
            @RequestParam(required = false) Integer duration) {

        if (name != null) return ResponseEntity.ok(serviceDetailsService.getByServiceName(name));
        if (price != null) return ResponseEntity.ok(serviceDetailsService.getByPrice(price));
        if (duration != null) return ResponseEntity.ok(serviceDetailsService.getByDuration(duration));

        // Dacă nu se dă niciun filtru, returnăm tot (trebuie sa ai metoda getAllServices in Service)
        // Dacă nu ai getAll, poți returna o listă goală sau implementa metoda.
        return ResponseEntity.ok(List.of());
    }

    // 2. CREATE
    @PostMapping
    public ResponseEntity<ServiceDetailsDTO> createService(@RequestBody @Valid ServiceDetailsDTO serviceDTO) {
        return new ResponseEntity<>(serviceDetailsService.createService(serviceDTO), HttpStatus.CREATED);
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