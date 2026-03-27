package com.hype.barbershop.Service;
import com.hype.barbershop.Model.DTO.AppointmentDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Metoda send email
    @Async
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // ATENTIE: Pune adresa ta validata in Brevo
            helper.setFrom("hypebarbershopiasi@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true indica faptul ca este HTML

            mailSender.send(message);
            System.out.println("Email HTML trimis cu succes catre: " + to);

        } catch (MessagingException e) {
            System.err.println("Eroare la trimiterea emailului: " + e.getMessage());
        }
    }

    //client html template

    public String generateClientTemplate(AppointmentDTO dto) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        String date = dto.getStartTime().format(dateFormatter);
        String time = dto.getStartTime().format(timeFormatter);

        return """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #ddd; border-radius: 10px; overflow: hidden; color: #333;">
                <div style="background-color: #1a1a1a; color: #fff; padding: 20px; text-align: center;">
                    <h2 style="margin: 0;">Hype Barbershop</h2>
                </div>
                <div style="padding: 20px; background-color: #fafafa;">
                    <p style="font-size: 16px;">Salut <strong>%s</strong>,</p>
                    <p style="font-size: 16px;">Programarea ta a fost confirmata cu succes! Iata detaliile:</p>
                    
                    <div style="background-color: #fff; padding: 15px; border-radius: 8px; border-left: 4px solid #d4af37; margin: 20px 0; box-shadow: 0 2px 4px rgba(0,0,0,0.05);">
                        <p style="margin: 5px 0;">✂️ <strong>Serviciu:</strong> %s</p>
                        <p style="margin: 5px 0;">💈 <strong>Frizer:</strong> %s</p>
                        <p style="margin: 5px 0;">📅 <strong>Data:</strong> %s</p>
                        <p style="margin: 5px 0;">⏰ <strong>Ora:</strong> %s</p>
                        <p style="margin: 5px 0;">💳 <strong>Pret serviciu:</strong> %.2f RON</p>
                    </div>
                    
                    <p style="font-size: 14px; color: #777;">Te rugam sa ajungi cu 5 minute inainte de ora stabilita. Daca intervin modificari, te rugam sa ne anunti la numarul +40 741 516 583.</p>
                </div>
                <div style="background-color: #1a1a1a; color: #aaa; text-align: center; padding: 10px; font-size: 12px;">
                    &copy; 2024 Hype Barbershop. Toate drepturile rezervate.
                </div>
            </div>
            """.formatted(dto.getClientName(), dto.getServiceName(), dto.getBarberName(), date, time, dto.getPrice());
    }

    // template fir barber
    public String generateBarberTemplate(AppointmentDTO dto) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        String date = dto.getStartTime().format(dateFormatter);
        String time = dto.getStartTime().format(timeFormatter);

        // Verificam daca exista informatii aditionale pentru a nu afisa 'null' in email
        String info = (dto.getAdditionalInfo() != null && !dto.getAdditionalInfo().isEmpty())
                ? dto.getAdditionalInfo()
                : "Nu exista notite suplimentare.";

        return """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #ddd; border-radius: 10px; overflow: hidden; color: #333;">
                <div style="background-color: #1a1a1a; color: #d4af37; padding: 20px; text-align: center;">
                    <h2 style="margin: 0;">Programare Noua</h2>
                </div>
                <div style="padding: 20px; background-color: #fafafa;">
                    <p style="font-size: 16px;">Salut <strong>%s</strong>,</p>
                    <p style="font-size: 16px;">O noua programare a fost adaugata in calendarul tau. Iata detaliile:</p>
                    
                    <div style="background-color: #fff; padding: 15px; border-radius: 8px; border-left: 4px solid #1a1a1a; margin: 20px 0; box-shadow: 0 2px 4px rgba(0,0,0,0.05);">
                        <p style="margin: 5px 0;">👤 <strong>Client:</strong> %s</p>
                        <p style="margin: 5px 0;">📞 <strong>Telefon:</strong> <a href="tel:%s">%s</a></p>
                        <p style="margin: 5px 0;">✂️ <strong>Serviciu:</strong> %s</p>
                        <p style="margin: 5px 0;">📅 <strong>Data:</strong> %s</p>
                        <p style="margin: 5px 0;">⏰ <strong>Ora:</strong> %s</p>
                        <p style="margin: 5px 0;">⏳ <strong>Durata:</strong> %d min</p>
                    </div>
                    
                    <p style="font-size: 14px; color: #555;"><strong>Notite client:</strong> %s</p>
                </div>
            </div>
            """.formatted(
                dto.getBarberName(),
                dto.getClientName(),
                dto.getPhoneNumber(), dto.getPhoneNumber(), // Pus de doua ori pentru a face link-ul apelabil pe telefon
                dto.getServiceName(),
                date,
                time,
                dto.getDuration(),
                info
        );
    }
}
