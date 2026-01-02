package com.hype.barbershop.ControllerUnitTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hype.barbershop.Controller.BarberControllerAPI;
import com.hype.barbershop.Model.DTO.BarberDTO;
import com.hype.barbershop.Model.DTO.BarberRegistrationDTO;
import com.hype.barbershop.Service.BarberService;
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

@WebMvcTest(BarberControllerAPI.class)
public class BarberControllerAPITests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BarberService barberService;

    private ObjectMapper objectMapper;

    private BarberDTO barberDTO;
    private BarberRegistrationDTO registrationDTO;

    @BeforeEach
    void setUp() {
        // 1. Configurare manuală ObjectMapper pentru a evita erorile de Autowiring
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // 2. Setup BarberDTO (Response / Update)
        barberDTO = new BarberDTO();
        barberDTO.setId(1L);
        barberDTO.setFirstName("Alex");
        barberDTO.setLastName("Barber");
        barberDTO.setEmail("alex@barber.com");
        barberDTO.setIsActive(true);
        // Dacă DTO-ul are câmpuri de rol, setează-le aici dacă sunt obligatorii

        // 3. Setup RegistrationDTO (Create)
        // Este crucial să setăm câmpurile care au @NotBlank în entitate/DTO
        registrationDTO = new BarberRegistrationDTO();
        registrationDTO.setFirstName("New");
        registrationDTO.setLastName("Barber");
        registrationDTO.setEmail("new@barber.com");
        registrationDTO.setRawPassword("securePass123");
    }

    // --------------------------------------------------------------------------------
    // 1. GET ACTIVE TESTS
    // --------------------------------------------------------------------------------

    @Test
    @DisplayName("Get Active Barbers - Success")
    void getActiveBarbers_Success() throws Exception {
        when(barberService.getIfActive()).thenReturn(List.of(barberDTO));

        mockMvc.perform(get("/api/barbers/active")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is("Alex")));

        verify(barberService).getIfActive();
    }

    // --------------------------------------------------------------------------------
    // 2. SEARCH TESTS
    // --------------------------------------------------------------------------------

    @Test
    @DisplayName("Search by Email - Found")
    void search_ByEmail_Found() throws Exception {
        String email = "alex@barber.com";
        when(barberService.getByEmail(email)).thenReturn(Optional.of(barberDTO));

        mockMvc.perform(get("/api/barbers/search")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(email)));

        verify(barberService).getByEmail(email);
    }

    @Test
    @DisplayName("Search by Email - Not Found (404)")
    void search_ByEmail_NotFound() throws Exception {
        String email = "unknown@barber.com";
        when(barberService.getByEmail(email)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/barbers/search")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(barberService).getByEmail(email);
    }

    @Test
    @DisplayName("Search by First Name - Success")
    void search_ByFirstName() throws Exception {
        String firstName = "Alex";
        when(barberService.getByFirstName(firstName)).thenReturn(List.of(barberDTO));

        mockMvc.perform(get("/api/barbers/search")
                        .param("firstName", firstName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is("Alex")));

        verify(barberService).getByFirstName(firstName);
    }

    @Test
    @DisplayName("Search by Last Name - Success")
    void search_ByLastName() throws Exception {
        String lastName = "Barber";
        when(barberService.getByLastName(lastName)).thenReturn(List.of(barberDTO));

        mockMvc.perform(get("/api/barbers/search")
                        .param("lastName", lastName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].lastName", is("Barber")));

        verify(barberService).getByLastName(lastName);
    }

    @Test
    @DisplayName("Search No Params - Returns Active (Default)")
    void search_NoParams_ReturnsActive() throws Exception {
        // Conform logicii din controller, dacă nu sunt parametri, returnează active
        when(barberService.getIfActive()).thenReturn(List.of(barberDTO));

        mockMvc.perform(get("/api/barbers/search")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(barberService).getIfActive();
    }

    // --------------------------------------------------------------------------------
    // 3. CREATE TESTS
    // --------------------------------------------------------------------------------

    @Test
    @DisplayName("Register Barber - Success (201 Created)")
    void createBarber_Success() throws Exception {
        when(barberService.createBarber(any(BarberRegistrationDTO.class))).thenReturn(barberDTO);

        mockMvc.perform(post("/api/barbers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is("Alex"))); // Returnează DTO-ul creat

        verify(barberService).createBarber(any(BarberRegistrationDTO.class));
    }

    // --------------------------------------------------------------------------------
    // 4. UPDATE TESTS
    // --------------------------------------------------------------------------------

    @Test
    @DisplayName("Update Barber - Success")
    void updateBarber_Success() throws Exception {
        BarberDTO updateRequest = new BarberDTO();
        updateRequest.setId(1L);
        updateRequest.setFirstName("UpdatedName");
        updateRequest.setLastName("UpdatedLast");
        updateRequest.setEmail("update@barber.com");
        // Important: asigură-te că toate câmpurile necesare validării sunt prezente

        BarberDTO responseDTO = new BarberDTO();
        responseDTO.setId(1L);
        responseDTO.setFirstName("UpdatedName");
        responseDTO.setLastName("UpdatedLast");

        when(barberService.updateBarber(eq(1L), any(BarberDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/barbers/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("UpdatedName")));

        verify(barberService).updateBarber(eq(1L), any(BarberDTO.class));
    }

    // --------------------------------------------------------------------------------
    // 5. DELETE TESTS
    // --------------------------------------------------------------------------------

    @Test
    @DisplayName("Delete Barber - Success (204 No Content)")
    void deleteBarber_Success() throws Exception {
        doNothing().when(barberService).deleteBarber(1L);

        mockMvc.perform(delete("/api/barbers/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(barberService, times(1)).deleteBarber(1L);
    }
}