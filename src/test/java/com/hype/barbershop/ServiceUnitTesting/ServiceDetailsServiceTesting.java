package com.hype.barbershop.ServiceUnitTesting;


import com.hype.barbershop.Exceptions.BarbershopDuplicateResource;
import com.hype.barbershop.Exceptions.BarbershopException;
import com.hype.barbershop.Exceptions.BarbershopResourceNotFound;
import com.hype.barbershop.Exceptions.IllegalBarbershopArgument;
import com.hype.barbershop.Model.DTO.ServiceDetailsDTO;
import com.hype.barbershop.Model.Entity.ServiceDetails;
import com.hype.barbershop.Model.Mapper.ServiceDetailsMapper;
import com.hype.barbershop.Repository.ServiceDetailsRepository;
import com.hype.barbershop.Service.ServiceDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
        when(serviceDetailsRepo.findByPrice(70d)).thenReturn(List.of(serviceDetails));
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

    @Test
    @DisplayName("Create service - happy path")
    void createService_HappyPath(){
        // 1. return empty list
        // use anyString() to avoid case-sensitivity error
        when(serviceDetailsRepo.findByServiceNameContainingIgnoreCase(anyString()))
                .thenReturn(Collections.emptyList());

        // 2. save mock
        when(serviceDetailsRepo.save(any(ServiceDetails.class))).thenReturn(serviceDetails);
        when(serviceMapper.toDTO(serviceDetails)).thenReturn(serviceDetailsDTO);
        when(serviceMapper.toEntity(serviceDetailsDTO)).thenReturn(serviceDetails);

        // 3. Execute
        ServiceDetailsDTO created = serviceDetailsService.createService(serviceDetailsDTO);

        // 4. Verify
        assertNotNull(created);
        verify(serviceDetailsRepo).save(any(ServiceDetails.class));
    }

    @Test
    @DisplayName("Create service - null input")
    void createService_NullInput_ThrowsException(){
        assertThrows(IllegalBarbershopArgument.class, () -> {
            serviceDetailsService.createService(null);
        });
    }

    @Test
    void createService_Duplicate_ThrowsException(){
        when(serviceDetailsRepo.findByServiceNameContainingIgnoreCase(anyString()))
                .thenReturn(List.of(serviceDetails));

        assertThrows(IllegalBarbershopArgument.class, ()->{
            serviceDetailsService.createService(serviceDetailsDTO);
        });

        verify(serviceDetailsRepo, never()).save(any());
    }

    @Test
    @DisplayName("Update service details - happy path")
    void updateServiceDetails_HappyPath(){

        //change dto for update
        ServiceDetailsDTO updateDTO = new ServiceDetailsDTO();
        updateDTO.setServiceName("Premium skin fade si barba");
        updateDTO.setId(2L);
        updateDTO.setDuration(100);
        updateDTO.setPrice(150d);

        when(serviceMapper.toEntity(updateDTO)).thenReturn(serviceDetails);
        when(serviceDetailsRepo.findById(2L)).thenReturn(Optional.of(serviceDetails));
        when(serviceDetailsRepo.save(any(ServiceDetails.class))).thenReturn(serviceDetails);
        when(serviceMapper.toDTO(any(ServiceDetails.class))).thenReturn(updateDTO);

        ServiceDetailsDTO result = serviceDetailsService.updateService(2L, updateDTO);

        assertEquals("Premium skin fade si barba", result.getServiceName());
        verify(serviceDetailsRepo).save(serviceDetails);
    }

    @Test
    @DisplayName("Update service details - not found throws exception ")
    void updateServiceDetails_NotFound(){
        when(serviceDetailsRepo.findById(9412L)).thenReturn(Optional.empty());

        assertThrows(BarbershopException.class, ()->{
            serviceDetailsService.updateService(9412L, serviceDetailsDTO);
        });

    }

    @Test
    @DisplayName("Update service details - null input throws exception")
    void updateServiceDetails_NullDTOInput(){

        assertThrows(IllegalBarbershopArgument.class, () -> {
            serviceDetailsService.updateService(1L, null);
        });
    }

    @Test
    @DisplayName("Delete service - null ID")
    void deleteService_NullId(){
        assertThrows(IllegalBarbershopArgument.class, ()-> {
            serviceDetailsService.deleteService(null);
        });
    }

    @Test
    @DisplayName("Delete service - not found ")
    void deleteService_NotFound(){
        when(serviceDetailsRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BarbershopResourceNotFound.class, ()->{
            serviceDetailsService.deleteService(99L);
        });
    }
}
