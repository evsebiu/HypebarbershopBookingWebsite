package com.hype.barbershop.ServiceUnitTesting;

import com.hype.barbershop.Exceptions.BarbershopException;
import com.hype.barbershop.Model.Entity.Barber;
import com.hype.barbershop.Model.Enums.Role;
import com.hype.barbershop.Repository.BarberRepository;
import com.hype.barbershop.Service.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTesting {
    @Mock
    private BarberRepository barberRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private Barber barber;

    @BeforeEach
    void setUp() {
        barber = new Barber();
        barber.setId(1L);
        barber.setEmail("test@barber.com");
        barber.setPassword("encodedPassword");
        barber.setRole(Role.ROLE_ADMIN);
    }

    @Test
    @DisplayName("Load user by username - Success")
    void loadUserByUsername_Success() {

        barber.setRole(Role.ROLE_BARBER);

        // 1. Mock
        when(barberRepository.findByEmail("test@barber.com")).thenReturn(Optional.of(barber));

        // 2. Execuție
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test@barber.com");

        // 3. Verificări
        assertNotNull(userDetails);
        assertEquals("test@barber.com", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());

        String expectedRole = Role.ROLE_BARBER.name();

        // Verificăm dacă rolul a fost convertit corect în Authority
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(expectedRole)));

        verify(barberRepository).findByEmail("test@barber.com");
    }

    @Test
    @DisplayName("Load user by username - Not Found - Throws Exception")
    void loadUserByUsername_NotFound() {
        // 1. Mock - returnăm empty
        when(barberRepository.findByEmail("nu_exista@barber.com")).thenReturn(Optional.empty());

        // 2. & 3. Execuție și verificare excepție
        assertThrows(BarbershopException.class, () -> {
            customUserDetailsService.loadUserByUsername("nu_exista@barber.com");
        });

        verify(barberRepository).findByEmail("nu_exista@barber.com");
    }
}
