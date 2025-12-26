package ServicePackageUnitTests;

import com.hype.barbershop.Model.DTO.AppointmentDTO;
import com.hype.barbershop.Model.Entity.Appointment;
import com.hype.barbershop.Model.Entity.Barber;
import com.hype.barbershop.Model.Entity.ServiceDetails;
import com.hype.barbershop.Model.Mapper.AppointmentMapper;
import com.hype.barbershop.Repository.AppointmentRepository;
import com.hype.barbershop.Repository.BarberRepository;
import com.hype.barbershop.Repository.ServiceDetailsRepository;
import com.hype.barbershop.Service.AppointmentService;
// Import your custom exception so the test knows what to catch
import com.hype.barbershop.Exceptions.RuntimeException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

// --- FIX: Use Mockito ArgumentMatchers, NOT Hamcrest ---
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {

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

    @Test
    void createAppointment_WhenNoOverlap_ShouldSave() {
        // Arrange
        Long barberId = 1L;
        Long serviceId = 2L;
        LocalDateTime start = LocalDateTime.of(2025, 12, 25, 10, 0);

        AppointmentDTO inputDto = new AppointmentDTO();
        inputDto.setBarberId(barberId);
        inputDto.setServiceId(serviceId);
        inputDto.setStartTime(start);
        inputDto.setClientName("John Doe");

        Barber barber = new Barber();
        barber.setId(barberId);

        ServiceDetails service = new ServiceDetails();
        service.setId(serviceId);
        service.setDuration(30);

        when(barberRepository.findById(barberId)).thenReturn(Optional.of(barber));
        when(serviceDetailsRepository.findById(serviceId)).thenReturn(Optional.of(service));
        when(appointmentRepository.findByBarberId(barberId)).thenReturn(Collections.emptyList());

        Appointment appointmentEntity = new Appointment();
        appointmentEntity.setId(100L);

        when(appointmentMapper.toEntity(inputDto)).thenReturn(appointmentEntity);
        // 'any(Appointment.class)' now works because it comes from Mockito
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointmentEntity);
        when(appointmentMapper.toDTO(any(Appointment.class))).thenReturn(inputDto);

        // Act
        AppointmentDTO result = appointmentService.createAppointment(inputDto);

        // Assert
        assertNotNull(result);
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void createAppointment_WhenOverlapExists_ShouldThrowException() {
        // Arrange
        Long barberId = 1L;
        LocalDateTime newStart = LocalDateTime.of(2025, 12, 25, 10, 0);

        AppointmentDTO inputDto = new AppointmentDTO();
        inputDto.setBarberId(barberId);
        inputDto.setServiceId(1L);
        inputDto.setStartTime(newStart);

        Barber barber = new Barber();
        barber.setId(barberId);

        ServiceDetails service = new ServiceDetails();
        service.setDuration(30);

        Appointment existing = new Appointment();
        existing.setId(50L);
        existing.setStartTime(LocalDateTime.of(2025, 12, 25, 10, 15));
        existing.setBarber(barber);
        existing.setServiceDetails(service);

        when(barberRepository.findById(barberId)).thenReturn(Optional.of(barber));
        when(serviceDetailsRepository.findById(1L)).thenReturn(Optional.of(service));
        when(appointmentRepository.findByBarberId(barberId)).thenReturn(List.of(existing));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            appointmentService.createAppointment(inputDto);
        });

        assertEquals("Intervalul orar este deja ocupat pentru acest frizer.", exception.getMessage());

        // Use any() for the verify check
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void updateAppointment_WhenIgnoringSelf_ShouldSuccess() {
        // Arrange
        Long appointmentId = 10L;
        Long barberId = 1L;
        LocalDateTime start = LocalDateTime.of(2025, 12, 25, 12, 0);

        AppointmentDTO updateDto = new AppointmentDTO();
        updateDto.setBarberId(barberId);
        updateDto.setServiceId(1L);
        updateDto.setStartTime(start);
        updateDto.setClientName("Updated Name");

        Barber barber = new Barber();
        barber.setId(barberId);

        ServiceDetails service = new ServiceDetails();
        service.setDuration(30);

        Appointment existingSelf = new Appointment();
        existingSelf.setId(appointmentId);
        existingSelf.setStartTime(start);
        existingSelf.setBarber(barber);
        existingSelf.setServiceDetails(service);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingSelf));
        when(barberRepository.findById(barberId)).thenReturn(Optional.of(barber));
        when(serviceDetailsRepository.findById(1L)).thenReturn(Optional.of(service));

        when(appointmentRepository.findByBarberId(barberId)).thenReturn(List.of(existingSelf));

        when(appointmentRepository.save(any(Appointment.class))).thenReturn(existingSelf);
        when(appointmentMapper.toDTO(any(Appointment.class))).thenReturn(updateDto);

        // Act
        AppointmentDTO result = appointmentService.updateAppointment(appointmentId, updateDto);

        // Assert
        assertNotNull(result);
        verify(appointmentRepository).save(existingSelf);
    }
}