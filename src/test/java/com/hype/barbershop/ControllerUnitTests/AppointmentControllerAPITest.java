package com.hype.barbershop.ControllerUnitTests;

import com.hype.barbershop.Controller.AppointmentControllerAPI;
import com.hype.barbershop.Model.DTO.AppointmentDTO;
import com.hype.barbershop.Service.AppointmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // Import necesar pentru date
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentControllerAPI.class)
public class AppointmentControllerAPITest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppointmentService appointmentService;

    // Am scos @Autowired pentru a evita eroarea "UnsatisfiedDependencyException"
    private ObjectMapper objectMapper;

    private AppointmentDTO appointmentDTO;

    @BeforeEach
    void setUp() {
        // Inițializăm manual ObjectMapper
        objectMapper = new ObjectMapper();
        // Înregistrăm modulul pentru a suporta LocalDateTime (Java 8 dates)
        objectMapper.registerModule(new JavaTimeModule());

        // Setup reusable DTO
        appointmentDTO = new AppointmentDTO();
        appointmentDTO.setId(1L);
        appointmentDTO.setClientName("John Doe");
        appointmentDTO.setClientEmail("john@example.com");
        appointmentDTO.setPhoneNumber("0722123456");
        // appointmentDTO.setAppointmentDate(LocalDateTime.now());
    }

    // --------------------------------------------------------------------------------
    // 1. CREATE TESTS
    // --------------------------------------------------------------------------------

    @Test
    @DisplayName("Create Appointment - Success (201 Created)")
    void createAppointment_Success() throws Exception {
        // Mock service behavior
        when(appointmentService.createAppointment(any(AppointmentDTO.class))).thenReturn(appointmentDTO);

        // Perform POST request
        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appointmentDTO)))
                .andExpect(status().isCreated()) // Expect 201
                .andExpect(jsonPath("$.clientName", is("John Doe")))
                .andExpect(jsonPath("$.clientEmail", is("john@example.com")));

        verify(appointmentService, times(1)).createAppointment(any(AppointmentDTO.class));
    }

    // --------------------------------------------------------------------------------
    // 2. GET ALL TESTS
    // --------------------------------------------------------------------------------

    @Test
    @DisplayName("Get All Appointments - Success")
    void getAll_Success() throws Exception {
        when(appointmentService.getAllAppointments()).thenReturn(List.of(appointmentDTO));

        mockMvc.perform(get("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect 200
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].clientName", is("John Doe")));

        verify(appointmentService, times(1)).getAllAppointments();
    }

    // --------------------------------------------------------------------------------
    // 3. SEARCH TESTS
    // --------------------------------------------------------------------------------

    @Test
    @DisplayName("Search by Phone - Success")
    void search_ByPhone() throws Exception {
        String phone = "0722123456";
        when(appointmentService.getByPhoneNumber(phone)).thenReturn(List.of(appointmentDTO));

        mockMvc.perform(get("/api/appointments/search")
                        .param("phone", phone)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].phoneNumber", is(phone)));

        verify(appointmentService).getByPhoneNumber(phone);
        verify(appointmentService, never()).getByEmail(anyString());
    }

    @Test
    @DisplayName("Search by Email - Success")
    void search_ByEmail() throws Exception {
        String email = "john@example.com";
        when(appointmentService.getByEmail(email)).thenReturn(List.of(appointmentDTO));

        mockMvc.perform(get("/api/appointments/search")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clientEmail", is(email)));

        verify(appointmentService).getByEmail(email);
    }

    @Test
    @DisplayName("Search by Name - Success")
    void search_ByName() throws Exception {
        String name = "John";
        when(appointmentService.getByClientName(name)).thenReturn(List.of(appointmentDTO));

        mockMvc.perform(get("/api/appointments/search")
                        .param("name", name)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clientName", is("John Doe")));

        verify(appointmentService).getByClientName(name);
    }

    @Test
    @DisplayName("Search without params - Returns All")
    void search_NoParams_ReturnsAll() throws Exception {
        when(appointmentService.getAllAppointments()).thenReturn(List.of(appointmentDTO));

        mockMvc.perform(get("/api/appointments/search")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(appointmentService).getAllAppointments();
    }

    // --------------------------------------------------------------------------------
    // 4. GET BY ID TESTS
    // --------------------------------------------------------------------------------

    @Test
    @DisplayName("Get By ID - Found")
    void getById_Found() throws Exception {
        when(appointmentService.getById(1L)).thenReturn(Optional.of(appointmentDTO));

        mockMvc.perform(get("/api/appointments/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("Get By ID - Not Found")
    void getById_NotFound() throws Exception {
        when(appointmentService.getById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/appointments/{id}", 99L))
                .andExpect(status().isNotFound()); // Expect 404
    }

    // --------------------------------------------------------------------------------
    // 5. UPDATE TESTS
    // --------------------------------------------------------------------------------

    @Test
    @DisplayName("Update Appointment - Success")
    void updateAppointment_Success() throws Exception {
        AppointmentDTO updatedDTO = new AppointmentDTO();
        updatedDTO.setId(1L);
        updatedDTO.setClientName("Jane Updated");
        updatedDTO.setClientEmail("jane@example.com");
        updatedDTO.setPhoneNumber("0722123456");
        when(appointmentService.updateAppointmentAPI(eq(1L), any(AppointmentDTO.class))).thenReturn(updatedDTO);

        mockMvc.perform(put("/api/appointments/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientName", is("Jane Updated")));
    }

    // --------------------------------------------------------------------------------
    // 6. DELETE TESTS
    // --------------------------------------------------------------------------------

    @Test
    @DisplayName("Delete Appointment - Success (204 No Content)")
    void deleteAppointment_Success() throws Exception {
        doNothing().when(appointmentService).deleteAppointment(1L);

        mockMvc.perform(delete("/api/appointments/{id}", 1L))
                .andExpect(status().isNoContent()); // Expect 204

        verify(appointmentService, times(1)).deleteAppointment(1L);
    }
}