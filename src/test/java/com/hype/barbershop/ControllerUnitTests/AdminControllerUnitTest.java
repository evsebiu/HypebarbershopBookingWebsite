package com.hype.barbershop.ControllerUnitTests;

import com.hype.barbershop.Controller.AdminController;
import com.hype.barbershop.Service.AppointmentService;
import com.hype.barbershop.Service.BarberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AdminControllerUnitTest {

    private MockMvc mockMvc;

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private BarberService barberService;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        // Initializes MockMvc in standalone mode for this specific controller
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }

    // --- Tests for toggleBarberStatus ---

    @Test
    void toggleBarberStatus_ValidId_ReturnsOkAndSuccessMessage() throws Exception {
        // Arrange
        Long barberId = 1L;
        doNothing().when(barberService).toggleBarberStatus(barberId);

        // Act & Assert
        mockMvc.perform(patch("/api/admin/barbers/toggle/{id}", barberId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Statusul frizerlui a fost actualizat"));

        verify(barberService, times(1)).toggleBarberStatus(barberId);
    }

    @Test
    void toggleBarberStatus_ServiceThrowsException_PropagatesException() {
        // Arrange
        Long barberId = 99L;
        doThrow(new RuntimeException("Barber not found")).when(barberService).toggleBarberStatus(barberId);

        // Act & Assert
        // Testing directly on the controller instance to easily assert the exact exception is thrown
        Exception exception = assertThrows(RuntimeException.class, () -> {
            adminController.toggleBarberStatus(barberId);
        });

        assertEquals("Barber not found", exception.getMessage());
        verify(barberService, times(1)).toggleBarberStatus(barberId);
    }

    // --- Tests for deleteBarber ---

    @Test
    void deleteBarber_ValidId_ReturnsOkAndSuccessMessage() throws Exception {
        // Arrange
        Long barberId = 2L;
        doNothing().when(barberService).deleteBarber(barberId);

        // Act & Assert
        mockMvc.perform(patch("/api/admin/barbers/delete/{id}", barberId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Frizerul a fost sters!"));

        verify(barberService, times(1)).deleteBarber(barberId);
    }

    @Test
    void deleteBarber_ServiceThrowsException_PropagatesException() {
        // Arrange
        Long barberId = 100L;
        doThrow(new IllegalArgumentException("Invalid ID")).when(barberService).deleteBarber(barberId);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            adminController.deleteBarber(barberId);
        });

        assertEquals("Invalid ID", exception.getMessage());
        verify(barberService, times(1)).deleteBarber(barberId);
    }

    // --- Tests for getAllBarbers ---


    @Test
    void getAllBarbers_WhenEmptyList_ReturnsEmptyJsonArrayAndOkStatus() throws Exception {
        // Arrange
        when(barberService.getAll()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/admin/barbers")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(barberService, times(1)).getAll();
    }
}