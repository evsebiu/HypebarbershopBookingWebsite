package com.hype.barbershop.ServiceUnitTesting;

import com.hype.barbershop.Exceptions.BarbershopDuplicateResource;
import com.hype.barbershop.Exceptions.BarbershopResourceNotFound;
import com.hype.barbershop.Exceptions.IllegalBarbershopArgument;
import com.hype.barbershop.Model.DTO.BarberDTO;
import com.hype.barbershop.Model.DTO.BarberRegistrationDTO;
import com.hype.barbershop.Model.Entity.Barber;
import com.hype.barbershop.Model.Enums.Role;
import com.hype.barbershop.Model.Mapper.BarberMapper;
import com.hype.barbershop.Repository.BarberRepository;
import com.hype.barbershop.Service.BarberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class BarberServiceUnitTesting {

    @Mock
    private BarberMapper barberMapper;

    @Mock
    private BarberRepository barberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;


    @InjectMocks
    private BarberService barberService;

    // re-usable test data
    private Barber barber;
    private BarberDTO barberDTO;
    private BarberRegistrationDTO registrationDTO;


    @BeforeEach
    void setUp(){

        // set up barber
        barber = new Barber();
        barber.setFirstName("Lucian");
        barber.setLastName("Catalin");
        barber.setId(1L);
        barber.setRole(Role.ROLE_BARBER);
        barber.setIsActive(true);
        barber.setEmail("catalin@hype.ro");
        barber.setPassword("encodedPassword");

        //set up barberDTO
        barberDTO = new BarberDTO();
        barberDTO.setId(1L);
        barberDTO.setFirstName("Lucian");
        barberDTO.setLastName("Catalin");
        barberDTO.setEmail("catalin@hype.ro");
        barberDTO.setRole(Role.ROLE_BARBER);
        barberDTO.setIsActive(true);

        // set up registration barber dto
        registrationDTO = new BarberRegistrationDTO();
        registrationDTO.setFirstName("Lucian");
        registrationDTO.setLastName("Catalin");
        registrationDTO.setEmail("catalin@hype.ro");
        registrationDTO.setIsAdmin(false);
        registrationDTO.setRawPassword("1234");
    }

    // GET tests
    @Test
    @DisplayName("Get active barbers - Success")
    void testGetIfActive(){
        when(barberRepository.findByIsActiveTrue()).thenReturn(List.of(barber));
        when(barberMapper.toDTO(barber)).thenReturn(barberDTO);

        List<BarberDTO> result = barberService.getIfActive();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("catalin@hype.ro", result.get(0).getEmail());
        verify(barberRepository, times(1)).findByIsActiveTrue();
    }

    @Test
    @DisplayName("Get by email - found")
    void testGetByEmail_Found(){
        when(barberRepository.findByEmail("catalin@hype.ro")).thenReturn(Optional.of(barber));
        when(barberMapper.toDTO(barber)).thenReturn(barberDTO);

        Optional<BarberDTO> result = barberService.getByEmail("catalin@hype.ro");

        assertTrue(result.isPresent());
        assertEquals("Lucian", result.get().getFirstName());
    }

    @Test
    @DisplayName("Get by email - not found")
    void testGetByEmail_NotFound(){
        when(barberRepository.findByEmail("email-inexistent@email.ro")).thenReturn(Optional.empty());

        Optional<BarberDTO> result = barberService.getByEmail("email-inexistent@email.ro");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Get by first name - found")
    void getByFirstName_Found(){
        when(barberRepository.findByFirstName("Lucian")).thenReturn(List.of(barber));
        when(barberMapper.toDTO(barber)).thenReturn(barberDTO);

        List<BarberDTO> result = barberService.getByFirstName("Lucian");

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Get by last name -  found")
    void getByLastName_Found(){
        when(barberRepository.findByLastName("Catalin")).thenReturn(List.of(barber));
        when(barberMapper.toDTO(barber)).thenReturn(barberDTO);

        List<BarberDTO> result = barberService.getByLastName("Catalin");

        assertEquals(1, result.size());
    }


    // CRUD tests

    @Test
    @DisplayName("Create Barber - Success")
    void createBarber_Success(){
        when(barberRepository.findByEmail(registrationDTO.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registrationDTO.getRawPassword())).thenReturn("encodedPassword");
        when(barberRepository.save(any(Barber.class))).thenReturn(barber);
        when(barberMapper.toDTO(barber)).thenReturn(barberDTO);

        BarberDTO created = barberService.createBarber(registrationDTO);

        assertNotNull(created);
        assertEquals("catalin@hype.ro", created.getEmail());
        verify(barberRepository).save(any(Barber.class));
    }

    @Test
    @DisplayName("Create Barber - Null input")
    void createBarber_NullInput_ThrowsException(){
        assertThrows(IllegalBarbershopArgument.class, ()-> {
            barberService.createBarber(null);
        });
    }

    @Test
    @DisplayName("Create barber - Duplicate email throws exception")
    void testCreateBarber_DuplicateEmail(){
        when(barberRepository.findByEmail(registrationDTO.getEmail())).thenReturn(Optional.of(barber));

        assertThrows(BarbershopDuplicateResource.class, () -> {
            barberService.createBarber(registrationDTO);
        });

        verify(barberRepository, never()).save(any());

    }


    // UPDATE TESTS

    @Test
    @DisplayName("Update barber - Success")
    void testUpdateBarber_Success(){

        // change dto for update
        BarberDTO updateDTO = new BarberDTO();
        updateDTO.setId(1L);
        updateDTO.setEmail("newEmail@hype.ro");
        updateDTO.setFirstName("NewFirstName");
        updateDTO.setLastName("NewLastName");
        updateDTO.setIsActive(false);

        when(barberRepository.findById(1L)).thenReturn(Optional.of(barber));
        // simulate that the new email doesn't exist on someone else
        when(barberRepository.existsByEmailAndIdNot("newEmail@hype.ro", 1L)).thenReturn(false);
        when(barberRepository.save(any(Barber.class))).thenReturn(barber);
        when(barberMapper.toDTO(any(Barber.class))).thenReturn(updateDTO);

        BarberDTO result = barberService.updateBarber(1L, updateDTO);

        assertEquals("newEmail@hype.ro", result.getEmail());
        verify(barberRepository).save(barber);
    }

    @Test
    @DisplayName("Update barber - Not found")
    void updateBarber_NotFound(){
        when(barberRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BarbershopResourceNotFound.class, () -> {
            barberService.updateBarber(99L, barberDTO);
        });
    }

    @Test
    @DisplayName("Update barber - Null DTO")
    void testUpdateBarber_NullDTO(){
        when(barberRepository.findById(1L)).thenReturn(Optional.of(barber));

        assertThrows(IllegalBarbershopArgument.class, () -> {
            barberService.updateBarber(1L, null);
        });
    }

    @Test
    @DisplayName("Update barber - Email taken by other")
    void testUpdate_EmailTaken(){
        BarberDTO updateDTO = new BarberDTO();
        updateDTO.setEmail("taken@hype.ro");

        when(barberRepository.findById(1L)).thenReturn(Optional.of(barber));

        //simulate that this email belongs to someone else ID
        when(barberRepository.existsByEmailAndIdNot("taken@hype.ro",1L)).thenReturn(true);

        assertThrows(IllegalBarbershopArgument.class, ()->{
            barberService.updateBarber(1L, updateDTO);
        });

        verify(barberRepository, never()).save(any());
    }

    // DELETE TESTS

    @Test
    @DisplayName("Delete barber - null id")
    void deleteBarber_NullId(){
        assertThrows(IllegalBarbershopArgument.class, () -> {
            barberService.deleteBarber(null);
        });
    }

    @Test
    @DisplayName("Delete barber - Not found")
    void testDeleteBarber_NotFound() {
        when(barberRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BarbershopResourceNotFound.class, () -> {
            barberService.deleteBarber(99L);
        });

        verify(barberRepository, never()).delete(any());
    }
}
