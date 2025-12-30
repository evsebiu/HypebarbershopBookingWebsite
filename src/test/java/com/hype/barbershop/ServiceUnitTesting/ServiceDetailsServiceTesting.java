package com.hype.barbershop.ServiceUnitTesting;


import com.hype.barbershop.Model.DTO.ServiceDetailsDTO;
import com.hype.barbershop.Model.Entity.ServiceDetails;
import com.hype.barbershop.Model.Mapper.ServiceDetailsMapper;
import com.hype.barbershop.Repository.ServiceDetailsRepository;
import com.hype.barbershop.Service.ServiceDetailsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceDetailsServiceTesting {

    @Mock
    ServiceDetailsMapper serviceMapper;

    @Mock
    ServiceDetailsRepository serviceDetailsRepo;

    @InjectMocks
    private ServiceDetailsService serviceDetailsService;

    // reusable data
    private ServiceDetails serviceDetails;
    private ServiceDetailsDTO serviceDetailsDTO;

    @BeforeEach
    void setUp(){
        // set up service details
        serviceDetails = new ServiceDetails();
        serviceDetails.setServiceName("Tuns si barba");
        serviceDetails.setDuration(30);
        serviceDetails.setPrice(70d);
        serviceDetails.setId(1L);

        // set up dto

        serviceDetailsDTO = new ServiceDetailsDTO();
        serviceDetailsDTO.setServiceName("Tuns si barba");
        serviceDetailsDTO.setDuration(30);
        serviceDetailsDTO.setPrice(70d);
        serviceDetailsDTO.setId(1L);

    }

    @Test
    @DisplayName("Get by service name - Success")
    void getByServiceName_Found(){
        when(serviceDetailsRepo.findByServiceNameContainingIgnoreCase("Tuns si barba")).thenReturn(List.of(serviceDetails));
        when(serviceMapper.toDTO(serviceDetails)).thenReturn(serviceDetailsDTO);

        List<ServiceDetailsDTO> result = serviceDetailsService.getByServiceName("Tuns si barba");

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Get by service price - success")
    void getByServicePrice_Found(){
        when(serviceDetailsRepo.findByPrice(30d)).thenReturn(List.of(serviceDetails));
        when(serviceMapper.toDTO(serviceDetails)).thenReturn(serviceDetailsDTO);

        List<ServiceDetailsDTO> result = serviceDetailsService.getByPrice(70d);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(serviceDetailsDTO, result.get(0));

        verify(serviceDetailsRepo, times(1)).findByPrice(70d);
        verify(serviceMapper, times(1)).toDTO(serviceDetails);

    }

    @Test
    @DisplayName("Get by service duration - success")
    void getByServiceDuration_Found(){
        when(serviceDetailsRepo.findByDuration(30)).thenReturn(List.of(serviceDetails));
        when(serviceMapper.toDTO(serviceDetails)).thenReturn(serviceDetailsDTO);

        List<ServiceDetailsDTO> result = serviceDetailsService.getByDuration(30);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(serviceDetailsDTO, result.get(0));
    }
}
