package com.hype.barbershop.ControllerUnitTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hype.barbershop.Controller.ServiceDetailsControllerAPI;
import com.hype.barbershop.Model.DTO.ServiceDetailsDTO;
import com.hype.barbershop.Service.ServiceDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ServiceDetailsControllerAPI.class)
public class ServiceDetailsControllerAPITests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ServiceDetailsService serviceDetailsService;

    private ObjectMapper objectMapper;
    private ServiceDetailsDTO serviceDTO;

    @BeforeEach
    void setUp() {
        // 1. Instanțiere manuală pentru a evita erorile de context Spring
        objectMapper = new ObjectMapper();

        // 2. Setup DTO valid (toate câmpurile obligatorii completate)
        // Conform entității ServiceDetails, avem nevoie de serviceName, price și duration
        serviceDTO = new ServiceDetailsDTO();
        serviceDTO.setId(1L);
        serviceDTO.setServiceName("Tuns Clasic");
        serviceDTO.setPrice(50.0);
        serviceDTO.setDuration(30);
    }

    // --------------------------------------------------------------------------------
    // 1. GET TESTS (Filtering)
    // --------------------------------------------------------------------------------

    @Test
    @DisplayName("Get Services - By Name")
    void getServices_ByName() throws Exception {
        String name = "Tuns Clasic";
        when(serviceDetailsService.getByServiceName(name)).thenReturn(List.of(serviceDTO));

        mockMvc.perform(get("/api/services")
                        .param("name", name)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].serviceName", is(name)));

        verify(serviceDetailsService).getByServiceName(name);
    }

    @Test
    @DisplayName("Get Services - By Price")
    void getServices_ByPrice() throws Exception {
        Double price = 50.0;
        when(serviceDetailsService.getByPrice(price)).thenReturn(List.of(serviceDTO));

        mockMvc.perform(get("/api/services")
                        .param("price", String.valueOf(price))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].price", is(price)));

        verify(serviceDetailsService).getByPrice(price);
    }

    @Test
    @DisplayName("Get Services - By Duration")
    void getServices_ByDuration() throws Exception {
        Integer duration = 30;
        when(serviceDetailsService.getByDuration(duration)).thenReturn(List.of(serviceDTO));

        mockMvc.perform(get("/api/services")
                        .param("duration", String.valueOf(duration))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].duration", is(duration)));

        verify(serviceDetailsService).getByDuration(duration);
    }

    @Test
    @DisplayName("Get Services - No Params (Returns Empty List per current implementation)")
    void getServices_NoParams() throws Exception {
        // În codul controller-ului, dacă nu sunt parametri, returnează List.of()
        mockMvc.perform(get("/api/services")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // --------------------------------------------------------------------------------
    // 2. CREATE TESTS
    // --------------------------------------------------------------------------------

    @Test
    @DisplayName("Create Service - Success (201 Created)")
    void createService_Success() throws Exception {
        when(serviceDetailsService.createService(any(ServiceDetailsDTO.class))).thenReturn(serviceDTO);

        mockMvc.perform(post("/api/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(serviceDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.serviceName", is("Tuns Clasic")))
                .andExpect(jsonPath("$.price", is(50.0)));

        verify(serviceDetailsService, times(1)).createService(any(ServiceDetailsDTO.class));
    }

    // --------------------------------------------------------------------------------
    // 3. UPDATE TESTS
    // --------------------------------------------------------------------------------

    @Test
    @DisplayName("Update Service - Success")
    void updateService_Success() throws Exception {
        ServiceDetailsDTO updatedDTO = new ServiceDetailsDTO();
        updatedDTO.setId(1L);
        updatedDTO.setServiceName("Tuns Modern");
        updatedDTO.setPrice(60.0);
        updatedDTO.setDuration(45); // Setăm toate câmpurile obligatorii

        when(serviceDetailsService.updateService(eq(1L), any(ServiceDetailsDTO.class))).thenReturn(updatedDTO);

        mockMvc.perform(put("/api/services/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceName", is("Tuns Modern")))
                .andExpect(jsonPath("$.price", is(60.0)));

        verify(serviceDetailsService).updateService(eq(1L), any(ServiceDetailsDTO.class));
    }

    // --------------------------------------------------------------------------------
    // 4. DELETE TESTS
    // --------------------------------------------------------------------------------

    @Test
    @DisplayName("Delete Service - Success (204 No Content)")
    void deleteService_Success() throws Exception {
        doNothing().when(serviceDetailsService).deleteService(1L);

        mockMvc.perform(delete("/api/services/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(serviceDetailsService, times(1)).deleteService(1L);
    }
}