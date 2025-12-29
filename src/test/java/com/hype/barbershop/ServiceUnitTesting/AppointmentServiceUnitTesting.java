package com.hype.barbershop.ServiceUnitTesting;

import com.hype.barbershop.Exceptions.BarbershopException;
import com.hype.barbershop.Model.DTO.AppointmentDTO;
import com.hype.barbershop.Model.Entity.Appointment;
import com.hype.barbershop.Model.Entity.Barber;
import com.hype.barbershop.Model.Entity.ServiceDetails;
import com.hype.barbershop.Model.Enums.Role;
import com.hype.barbershop.Model.Mapper.AppointmentMapper;
import com.hype.barbershop.Repository.AppointmentRepository;
import com.hype.barbershop.Repository.BarberRepository;
import com.hype.barbershop.Repository.ServiceDetailsRepository;
import com.hype.barbershop.Service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceUnitTesting {

    @Mock
    private AppointmentMapper appointmentMapper;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private ServiceDetailsRepository serviceDetailsRepository;

    @Mock
    private BarberRepository barberRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    // re-usable test data
    private Barber barber;
    private ServiceDetails serviceDetails;
    private Appointment appointment;
    private AppointmentDTO appointmentDTO;
    private LocalDateTime startTime;


    // Test objects setUp

    @BeforeEach
    void setUp(){
        startTime = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);

        barber = new Barber();
        barber.setFirstName("Lucian");
        barber.setLastName("Catalin");
        barber.setId(1L);
        barber.setRole(Role.ROLE_BARBER);
        barber.setIsActive(true);
        barber.setPassword("1234");
        barber.setEmail("catalin@hype.ro");

        serviceDetails = new ServiceDetails();
        serviceDetails.setId(10L);
        serviceDetails.setServiceName("Tuns clasic");
        serviceDetails.setPrice(50d);
        serviceDetails.setDuration(35);

        appointment = new Appointment();
        appointment.setId(100L);
        appointment.setBarber(barber);
        appointment.setServiceDetails(serviceDetails);
        appointment.setStartTime(startTime);
        appointment.setPhoneNumber("+40213290184");
        appointment.setClientName("Eusebiu");
        appointment.setClientEmail("evsebiu@gmail.com");
        appointment.setAdditionalInfo("S-ar putea sa intarzii 5/10 minute maxim.");

        appointmentDTO = new AppointmentDTO();
        appointmentDTO.setBarberId(1L);
        appointmentDTO.setServiceId(10L);
        appointmentDTO.setStartTime(startTime);
        appointmentDTO.setPhoneNumber("+40713290184");
        appointmentDTO.setClientName("Eusebiu");
        appointmentDTO.setClientEmail("evsebiu@gmail.com");
        appointmentDTO.setAdditionalInfo("S-ar putea sa intarzii 5/10 minute maxim.");
    }

    // test will contain comments at every step


    // GET tests
    @Test
    void getAppointments_ShouldReturnList(){
        // 1. arrange mock
        //when repo is asked by all appointments he returns list of all
        when(appointmentRepository.findAll()).thenReturn(List.of(appointment));

        // when mapper get entity he returns dto
        when(appointmentMapper.toDTO(appointment)).thenReturn(appointmentDTO);

        // 2. act - call real method

        var result = appointmentService.getAllAppointments();

        // 3. assert - check result

        assertFalse(result.isEmpty(), "Lista nu ar trebui sa fie goala");
        assertEquals(1, result.size(), " Ar trebui sa existe doar un element.");
        assertEquals("Eusebiu", result.get(0).getClientName());
    }

    @Test
    void getById_WhenFound_ShouldReturnDTO(){

        // 1. arrange mock
        when(appointmentRepository.findById(100L)).thenReturn(Optional.of(appointment));
        when(appointmentMapper.toDTO(appointment)).thenReturn(appointmentDTO);

        // 2. act - call real method

        var result = appointmentService.getById(100L);

        // 3. assert

       assertTrue(result.isPresent());
       assertEquals(appointmentDTO, result.get());
    }

    @Test
    void getById_WhenNotFound_ShouldReturnEmpty(){

        when(appointmentService.getById(999L)).thenReturn(Optional.empty());

        var result = appointmentRepository.findById(999L);

        assertTrue(result.isEmpty(), "Ar trebui sa fie empty pentru un ID neexistent");
    }

    @Test
    void getByPhoneNumber_ShouldReturnList(){
        String phoneNumber = "+40713290184";

        when(appointmentRepository.findByPhoneNumber(phoneNumber)).thenReturn(List.of(appointment));
        when(appointmentMapper.toDTO(appointment)).thenReturn(appointmentDTO);

        var result = appointmentService.getByPhoneNumber(phoneNumber);

        assertEquals(1, result.size());
        assertEquals(phoneNumber, result.get(0).getPhoneNumber());
    }


    // TESTS FOR CREATE || business logic and validations

    @Test
    void createAppointment_HappyPath_Success(){

        // 1. arrange
        // we simulate that barber and service exists in database
        when(barberRepository.findById(1L)).thenReturn(Optional.of(barber));
        when(serviceDetailsRepository.findById(10L)).thenReturn(Optional.of(serviceDetails));

        // simulate that other appointments doesn't exist
        when(appointmentRepository.findByBarberId(1L)).thenReturn(Collections.emptyList());

        // save() returns saved object ( usually with generated ID )
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        when(appointmentMapper.toDTO(appointment)).thenReturn(appointmentDTO);
        when(appointmentMapper.toEntity(appointmentDTO)).thenReturn(appointment);

        // 2. ACT
        AppointmentDTO result = appointmentService.createAppointment(appointmentDTO);

        // 3. Assert
        assertNotNull(result);
        // verify that save() method was called once
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void createAppointment_WhenBarberNotFound_ThrowsException(){
        //simulate that barber it's not found
        when(barberRepository.findById(1l)).thenReturn(Optional.empty());

        // verify that we throw right exception
        assertThrows(BarbershopException.class, ()-> {
            appointmentService.createAppointment(appointmentDTO);
        });

        //verify that we didn't reach save phase
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void createAppointment_OverlapDetected_ThrowException(){
        // Scenario :  we want to book an appointment 35 mins, at 10am -> final 10:35am
        // already exists one from 10:15am to 10:50am - > they are overlapping

        when(barberRepository.findById(1L)).thenReturn(Optional.of(barber));
        when(serviceDetailsRepository.findById(10L)).thenReturn(Optional.of(serviceDetails));

        // create appointment that makes conflict
        Appointment conflictAppointment = new Appointment();
        conflictAppointment.setId(100L);
        conflictAppointment.setBarber(barber);
        conflictAppointment.setServiceDetails(serviceDetails); // 35 mins duration
        conflictAppointment.setStartTime(startTime.plusMinutes(15)); // starts at 10:15am


        // return list that contains conflict
        when(appointmentRepository.findByBarberId(1L)).thenReturn(List.of(conflictAppointment));

        // execute and wait for error
        Exception exception = assertThrows(BarbershopException.class, () -> {
            appointmentService.createAppointment(appointmentDTO);
        });

        assertEquals("Intervalul orar este deja ocupat pentru acest frizer.", exception.getMessage());
    }

    //TESTS FOR UPDATE
    @Test
    void updateAppointment_Success() {
        //find old appointment
        when(appointmentRepository.findById(100L)).thenReturn(Optional.of(appointment));

        //find barber and service (for validation)
        when(barberRepository.findById(1L)).thenReturn(Optional.of(barber));
        when(serviceDetailsRepository.findById(10L)).thenReturn(Optional.of(serviceDetails));

        //List of appointments : return current appointment
        //logic from service has to ignore based on excluded ID
        when(appointmentRepository.findByBarberId(1L)).thenReturn(List.of(appointment));

        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        when(appointmentMapper.toDTO(appointment)).thenReturn(appointmentDTO);

        AppointmentDTO result = appointmentService.updateAppointment(100L, appointmentDTO);
        assertNotNull(result);
        assertEquals("Eusebiu", result.getClientName());
    }

    @Test
    void updateAppointment_NotFound_ThrowsException(){
        when(appointmentRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(BarbershopException.class, () -> {
            appointmentService.updateAppointment(100L, appointmentDTO);
        });
    }

    // DELETE TESTS

    @Test
    void deleteAppointment_Success(){
        // simulate that exists
        when(appointmentRepository.existsById(100L)).thenReturn(true);

        appointmentService.deleteAppointment(100L);

        //verify if delete was called
        verify(appointmentRepository).deleteById(100L);
    }

    @Test
    void deleteAppointment_NotFound_ThrowsException(){
        //simulate that doesn't exist
        when(appointmentRepository.existsById(999L)).thenReturn(false);

        assertThrows(BarbershopException.class, () ->
                appointmentService.deleteAppointment(999L));

        // verify that delete wasn't called

        verify(appointmentRepository, never()).deleteById(any());
    }
}
