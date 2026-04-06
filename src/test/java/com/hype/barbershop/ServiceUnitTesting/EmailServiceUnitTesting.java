package com.hype.barbershop.ServiceUnitTesting;

import com.hype.barbershop.Model.DTO.AppointmentDTO;
import com.hype.barbershop.Service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceUnitTesting {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private AppointmentDTO appointmentDTO;

    @InjectMocks
    private EmailService emailService;

    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        // We use a real MimeMessage with a null Session to avoid deep-mocking issues with MimeMessageHelper
        mimeMessage = new MimeMessage((Session) null);
    }

    // --- Tests for sendHtmlEmail ---

    @Test
    void sendHtmlEmail_ValidInputs_SendsEmailSuccessfully() {
        // Arrange
        String to = "client@example.com";
        String subject = "Test Subject";
        String htmlBody = "<h1>Hello</h1>";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        emailService.sendHtmlEmail(to, subject, htmlBody);

        // Assert
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void sendHtmlEmail_MessagingExceptionThrown_CatchesAndSuppressesException() {
        // Arrange
        String to = "client@example.com";
        String subject = "Test Subject";
        String htmlBody = "<h1>Hello</h1>";

        // We mock MimeMessage to throw a MessagingException when modified by the Helper
        MimeMessage mockMimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mockMimeMessage);

        try {
            // Trigger an exception when the MimeMessageHelper tries to set the subject
            doThrow(new MessagingException("Simulated Mail Error")).when(mockMimeMessage).setSubject(any(), any());
        } catch (MessagingException e) {
            fail("Setup failed");
        }

        // Act
        // The service method should catch the exception internally and NOT throw it to the caller
        assertDoesNotThrow(() -> emailService.sendHtmlEmail(to, subject, htmlBody));

        // Assert
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class)); // Shouldn't reach the send method
    }

    // --- Tests for generateClientTemplate ---

    @Test
    void generateClientTemplate_ValidDto_ReturnsFormattedHtmlString() {
        // Arrange
        LocalDateTime appointmentTime = LocalDateTime.of(2026, 4, 15, 14, 30);
        when(appointmentDTO.getStartTime()).thenReturn(appointmentTime);
        when(appointmentDTO.getClientName()).thenReturn("John Doe");
        when(appointmentDTO.getServiceName()).thenReturn("Tuns & Barba");
        when(appointmentDTO.getBarberName()).thenReturn("Alex");
        when(appointmentDTO.getPrice()).thenReturn(100.0);

        // Act
        String result = emailService.generateClientTemplate(appointmentDTO);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("John Doe"));
        assertTrue(result.contains("Tuns &amp; Barba") || result.contains("Tuns & Barba"));
        assertTrue(result.contains("Alex"));
        assertTrue(result.contains("15.04.2026"));
        assertTrue(result.contains("14:30"));
        assertTrue(result.contains("100.00"));
    }

    @Test
    void generateClientTemplate_NullStartTime_ThrowsNullPointerException() {
        // Arrange
        when(appointmentDTO.getStartTime()).thenReturn(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> emailService.generateClientTemplate(appointmentDTO));
    }

    // --- Tests for generateBarberTemplate ---

    @Test
    void generateBarberTemplate_WithAdditionalInfo_ReturnsTemplateWithInfo() {
        // Arrange
        LocalDateTime appointmentTime = LocalDateTime.of(2026, 4, 15, 14, 30);
        when(appointmentDTO.getStartTime()).thenReturn(appointmentTime);
        when(appointmentDTO.getBarberName()).thenReturn("Alex");
        when(appointmentDTO.getClientName()).thenReturn("John Doe");
        when(appointmentDTO.getPhoneNumber()).thenReturn("0740123456");
        when(appointmentDTO.getServiceName()).thenReturn("Tuns");
        when(appointmentDTO.getDuration()).thenReturn(45);
        when(appointmentDTO.getAdditionalInfo()).thenReturn("Clientul doreste un fade pierdut.");

        // Act
        String result = emailService.generateBarberTemplate(appointmentDTO);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Clientul doreste un fade pierdut."));
        assertFalse(result.contains("Nu exista notite suplimentare."));
    }

    @Test
    void generateBarberTemplate_WithNullAdditionalInfo_ReturnsTemplateWithDefaultMessage() {
        // Arrange
        LocalDateTime appointmentTime = LocalDateTime.of(2026, 4, 15, 14, 30);
        when(appointmentDTO.getStartTime()).thenReturn(appointmentTime);
        when(appointmentDTO.getBarberName()).thenReturn("Alex");
        when(appointmentDTO.getClientName()).thenReturn("John Doe");
        when(appointmentDTO.getPhoneNumber()).thenReturn("0740123456");
        when(appointmentDTO.getServiceName()).thenReturn("Tuns");
        when(appointmentDTO.getDuration()).thenReturn(45);

        when(appointmentDTO.getAdditionalInfo()).thenReturn(null);

        // Act
        String result = emailService.generateBarberTemplate(appointmentDTO);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Nu exista notite suplimentare."));
    }

    @Test
    void generateBarberTemplate_WithEmptyAdditionalInfo_ReturnsTemplateWithDefaultMessage() {
        // Arrange
        LocalDateTime appointmentTime = LocalDateTime.of(2026, 4, 15, 14, 30);
        when(appointmentDTO.getStartTime()).thenReturn(appointmentTime);
        // ... (other mocks kept minimal to satisfy the .formatted requirements)
        when(appointmentDTO.getBarberName()).thenReturn("Alex");
        when(appointmentDTO.getClientName()).thenReturn("John Doe");
        when(appointmentDTO.getPhoneNumber()).thenReturn("0740123456");
        when(appointmentDTO.getServiceName()).thenReturn("Tuns");
        when(appointmentDTO.getDuration()).thenReturn(45);

        when(appointmentDTO.getAdditionalInfo()).thenReturn("");

        // Act
        String result = emailService.generateBarberTemplate(appointmentDTO);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Nu exista notite suplimentare."));
    }
}