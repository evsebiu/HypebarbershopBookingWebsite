package com.hype.barbershop.ServiceUnitTesting;

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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

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
        appointmentDTO.setPhoneNumber("+40213290184");
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

        // when mapper recieves entity he returns dto
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

        var restult = appointmentService.getById()
    }

}
