package com.hype.barbershop.Service;

import com.hype.barbershop.Model.DTO.AppointmentDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("hypebarbershopiasi@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);
            System.out.println("Email HTML trimis cu succes catre: " + to);

        } catch (MessagingException e) {
            System.err.println("Eroare la trimiterea emailului: " + e.getMessage());
        }
    }

    // TEMPLATE CLIENT
    // ==========================================
    // TEMPLATE CLIENT ACTUALIZAT
    // ==========================================
    // TEMPLATE CLIENT ACTUALIZAT - TEMA GRI DESCHIS
    // ==========================================
    public String generateClientTemplate(AppointmentDTO dto) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        String date = dto.getStartTime().format(dateFormatter);
        String time = dto.getStartTime().format(timeFormatter);

        return """
    <!DOCTYPE html>
    <html lang="ro">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <style type="text/css">
            body, table, td, a { -webkit-text-size-adjust: 100%%; -ms-text-size-adjust: 100%%; }
            img { -ms-interpolation-mode: bicubic; border: 0; outline: none; text-decoration: none; }
            /* Previne transformarea numerelor de telefon in link-uri albastre pe iOS */
            a[x-apple-data-detectors] { color: inherit !important; text-decoration: none !important; }
        </style>
    </head>
    <body style="margin:0; padding:0; background-color:#eaeaea;">

    <table width="100%%" cellpadding="0" cellspacing="0" border="0" bgcolor="#eaeaea" style="background-color: #eaeaea;">
        <tr>
            <td align="center" style="padding: 20px 0;">

                <table width="600" cellpadding="0" cellspacing="0" border="0" style="max-width: 600px;">

                    <tr>
                        <td align="center" style="padding:40px 30px 30px 30px;">
                            <h2 style="color:#c5a059; font-family:Arial, sans-serif; font-size: 24px; margin:0; letter-spacing: 2px;">
                                HYPE <span style="color:#333333">BARBERSHOP</span>
                            </h2>
                            <div style="width: 40px; height: 1px; background-color: #c5a059; margin-top: 15px;"></div>
                        </td>
                    </tr>

                    <tr>
                        <td align="center" style="padding:0 20px 30px 20px;">
                            <h1 style="color:#111111; font-family:Arial, sans-serif; margin:0 0 15px 0; font-size: 28px;">
                                Programare Reușită
                            </h1>
                            <p style="color:#444444; font-family:Arial, sans-serif; font-size: 16px; line-height: 24px; margin:0;">
                                Salut <b>%s</b>, te așteptăm cu plăcere la salon.<br>Mai jos regăsești detaliile vizitei tale.
                            </p>
                        </td>
                    </tr>

                    <tr>
                        <td align="center" style="padding: 0 20px;">

                            <table width="100%%" style="max-width: 400px; margin:0 auto; background-color: #f5f5f5; border:1px solid #dcdcdc; border-radius: 12px; border-top: 4px solid #c5a059;" cellpadding="0" cellspacing="0" border="0" bgcolor="#f5f5f5">
                                
                                <tr>
                                    <td align="center" style="padding:25px 20px 10px 20px;">
                                        <p style="color:#c5a059; font-family:Arial, sans-serif; font-size:12px; font-weight:bold; letter-spacing:1px; margin:0; text-transform:uppercase;">Detalii Rezervare</p>
                                        <div style="width: 100%%; height: 1px; background-color: #dcdcdc; margin-top: 15px;"></div>
                                    </td>
                                </tr>

                                <tr>
                                    <td align="center" style="padding:15px 20px 5px 20px;">
                                        <p style="color:#666666; font-family:Arial, sans-serif; margin:0; font-size:14px;">Serviciu</p>
                                        <p style="color:#111111; font-family:Arial, sans-serif; margin:5px 0 0 0; font-weight:bold; font-size:15px;">%s</p>
                                    </td>
                                </tr>

                                <tr>
                                    <td align="center" style="padding:15px 20px 5px 20px;">
                                        <p style="color:#666666; font-family:Arial, sans-serif; margin:0; font-size:14px;">Specialist</p>
                                        <p style="color:#111111; font-family:Arial, sans-serif; margin:5px 0 0 0; font-weight:bold; font-size:15px;">%s</p>
                                    </td>
                                </tr>

                                <tr>
                                    <td align="center" style="padding:15px 20px 5px 20px;">
                                        <p style="color:#666666; font-family:Arial, sans-serif; margin:0; font-size:14px;">Dată</p>
                                        <p style="color:#111111; font-family:Arial, sans-serif; margin:5px 0 0 0; font-weight:bold; font-size:15px;">%s</p>
                                    </td>
                                </tr>

                                <tr>
                                    <td align="center" style="padding:15px 20px 25px 20px;">
                                        <p style="color:#666666; font-family:Arial, sans-serif; margin:0; font-size:14px;">Ora</p>
                                        <p style="color:#c5a059; font-family:Arial, sans-serif; margin:5px 0 0 0; font-weight:bold; font-size:18px;">%s</p>
                                    </td>
                                </tr>

                                <tr>
                                    <td align="center" style="padding:25px 20px; border-top:1px solid #dcdcdc; background-color: #e2e2e2; border-radius: 0 0 12px 12px;">
                                        <p style="color:#666666; font-family:Arial, sans-serif; margin:0 0 5px 0; font-size:14px;">Total de plată</p>
                                        <p style="color:#111111; font-family:Arial, sans-serif; margin:0; font-size:24px; font-weight:bold;">
                                            %.2f RON
                                        </p>
                                    </td>
                                </tr>

                            </table>

                        </td>
                    </tr>

                    <tr>
                        <td align="center" style="padding:40px 20px;">
                            <p style="color:#444444; font-family:Arial, sans-serif; font-size:14px; margin:0 0 25px 0; line-height: 22px;">
                                Te rugăm să ajungi cu <b style="color:#c5a059;">5 minute înainte</b>.<br>Pentru anulări sau modificări, sună-ne direct:
                            </p>
                            
                            <table border="0" cellpadding="0" cellspacing="0">
                                <tr>
                                    <td align="center" bgcolor="#c5a059" style="border-radius: 50px;">
                                        <a href="tel:+40741516583"
                                           style="background-color:#c5a059; color:#ffffff; padding:18px 45px; border-radius:50px; text-decoration:none; font-family:Arial, sans-serif; font-weight:bold; font-size:14px; display:inline-block; text-transform:uppercase; letter-spacing:1px;">
                                            📞 Sună la Salon
                                        </a>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>

                </table>

            </td>
        </tr>
    </table>

    </body>
    </html>
    """.formatted(
                dto.getClientName(),
                dto.getServiceName(),
                dto.getBarberName(),
                date,
                time,
                dto.getPrice()
        );
    }
    // ==========================================
    // TEMPLATE FRIZER
    // ==========================================
    public String generateBarberTemplate(AppointmentDTO dto) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        String date = dto.getStartTime().format(dateFormatter);
        String time = dto.getStartTime().format(timeFormatter);

        String info = (dto.getAdditionalInfo() != null && !dto.getAdditionalInfo().isEmpty())
                ? dto.getAdditionalInfo()
                : "Nu exista notite suplimentare.";

        return """
               <!DOCTYPE html>
               <html lang="ro" xmlns="http://www.w3.org/1999/xhtml">
               <head>
                   <meta charset="utf-8">
                   <meta name="viewport" content="width=device-width, initial-scale=1.0">
                   <meta http-equiv="X-UA-Compatible" content="IE=edge">
                   <meta name="color-scheme" content="light dark">
                   <meta name="supported-color-schemes" content="light dark">
                   <title>Programare Nouă - Hype Barbershop</title>

                   <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600&family=Playfair+Display:ital,wght@0,600;1,600&display=swap" rel="stylesheet">
                   <style type="text/css">
                       :root { color-scheme: light dark; supported-color-schemes: light dark; }
                       body, table, td, a { -webkit-text-size-adjust: 100%%; -ms-text-size-adjust: 100%%; }
                       table, td { mso-table-lspace: 0pt; mso-table-rspace: 0pt; }
                       img { -ms-interpolation-mode: bicubic; border: 0; outline: none; text-decoration: none; }
                       body { margin: 0 !important; padding: 0 !important; width: 100%% !important; background-color: #0a0a0a !important; }
                       
                       @media screen and (max-width: 600px) {
                           .container { width: 100%% !important; padding: 0 15px !important; }
                           .hero-title { font-size: 28px !important; line-height: 36px !important; }
                           .card-wrapper { padding: 20px !important; }
                           .receipt-row td { display: block !important; width: 100%% !important; text-align: left !important; }
                           .receipt-value { padding-top: 5px !important; padding-bottom: 15px !important; }
                       }
                   </style>
               </head>
               <body style="background-color: #0a0a0a; margin: 0; padding: 0; -webkit-font-smoothing: antialiased;">
                   <table border="0" cellpadding="0" cellspacing="0" width="100%%" bgcolor="#0a0a0a" style="background-color: #0a0a0a; background-image: url('data:image/svg+xml,%%3Csvg xmlns=&#39;http://www.w3.org/2000/svg&#39; width=&#39;1&#39; height=&#39;1&#39;%%3E%%3Crect width=&#39;1&#39; height=&#39;1&#39; fill=&#39;%%230a0a0a&#39;/%%3E%%3C/svg%%3E');">
                       <tr>
                           <td align="center" style="padding: 40px 0;">
                               <table border="0" cellpadding="0" cellspacing="0" width="600" class="container" style="background-color: transparent;">
                                   <tr>
                                       <td align="center" style="padding-bottom: 40px;">
                                           <h2 style="margin: 0; font-family: 'Playfair Display', Georgia, serif; font-size: 20px; font-weight: 600; color: #c5a059; letter-spacing: 2px; text-transform: uppercase;">
                                               HYPE <span style="color: #f5f5f7;">ADMIN</span>
                                           </h2>
                                       </td>
                                   </tr>
                                   <tr>
                                       <td align="center" style="padding: 0 20px 30px 20px;">
                                           <h1 class="hero-title" style="margin: 0; font-family: 'Playfair Display', Georgia, serif; font-size: 36px; font-weight: 600; color: #ffffff; line-height: 42px; margin-bottom: 15px;">
                                               Programare Nouă
                                           </h1>
                                           <p style="margin: 0; font-family: 'Inter', Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 300; color: #a0a0a0; line-height: 24px;">
                                               Salut <strong style="color: #ffffff;">%s</strong>,<br> O nouă rezervare a fost adăugată în calendarul tău.
                                           </p>
                                       </td>
                                   </tr>
                                   <tr>
                                       <td align="center" style="padding: 0 20px;">
                                           <table border="0" cellpadding="0" cellspacing="0" width="100%%" class="card-wrapper" bgcolor="#141414" style="background-color: #141414; background-image: url('data:image/svg+xml,%%3Csvg xmlns=&#39;http://www.w3.org/2000/svg&#39; width=&#39;1&#39; height=&#39;1&#39;%%3E%%3Crect width=&#39;1&#39; height=&#39;1&#39; fill=&#39;%%23141414&#39;/%%3E%%3C/svg%%3E'); border: 1px solid #2a2a2a; border-radius: 12px; border-top: 3px solid #c5a059;">
                                               <tr>
                                                   <td style="padding: 30px;">
                                                       <table border="0" cellpadding="0" cellspacing="0" width="100%%">
                                                           <tr class="receipt-row">
                                                               <td align="left" style="padding-bottom: 15px; font-family: 'Inter', Helvetica, Arial, sans-serif; font-size: 14px; color: #a0a0a0;">Client</td>
                                                               <td align="right" class="receipt-value" style="padding-bottom: 15px; font-family: 'Inter', Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 600; color: #ffffff;">%s</td>
                                                           </tr>
                                                           <tr class="receipt-row">
                                                               <td align="left" style="padding-bottom: 15px; font-family: 'Inter', Helvetica, Arial, sans-serif; font-size: 14px; color: #a0a0a0;">Telefon</td>
                                                               <td align="right" class="receipt-value" style="padding-bottom: 15px; font-family: 'Inter', Helvetica, Arial, sans-serif; font-size: 15px; font-weight: 600;">
                                                                   <a href="tel:%s" style="color: #c5a059; text-decoration: none;">%s</a>
                                                               </td>
                                                           </tr>
                                                           <tr class="receipt-row">
                                                               <td align="left" style="padding-bottom: 15px; font-family: 'Inter', Helvetica, Arial, sans-serif; font-size: 14px; color: #a0a0a0;">Serviciu</td>
                                                               <td align="right" class="receipt-value" style="padding-bottom: 15px; font-family: 'Inter', Helvetica, Arial, sans-serif; font-size: 15px; font-weight: 600; color: #ffffff;">%s</td>
                                                           </tr>
                                                           <tr class="receipt-row">
                                                               <td align="left" style="padding-bottom: 15px; font-family: 'Inter', Helvetica, Arial, sans-serif; font-size: 14px; color: #a0a0a0;">Dată</td>
                                                               <td align="right" class="receipt-value" style="padding-bottom: 15px; font-family: 'Inter', Helvetica, Arial, sans-serif; font-size: 15px; font-weight: 600; color: #ffffff;">%s</td>
                                                           </tr>
                                                           <tr class="receipt-row">
                                                               <td align="left" style="padding-bottom: 15px; font-family: 'Inter', Helvetica, Arial, sans-serif; font-size: 14px; color: #a0a0a0;">Ora</td>
                                                               <td align="right" class="receipt-value" style="padding-bottom: 15px; font-family: 'Inter', Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 600; color: #c5a059;">%s</td>
                                                           </tr>
                                                           <tr class="receipt-row">
                                                               <td align="left" style="padding-bottom: 25px; border-bottom: 1px solid #2a2a2a; font-family: 'Inter', Helvetica, Arial, sans-serif; font-size: 14px; color: #a0a0a0;">Durată est.</td>
                                                               <td align="right" class="receipt-value" style="padding-bottom: 25px; border-bottom: 1px solid #2a2a2a; font-family: 'Inter', Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 600; color: #ffffff;">%d min</td>
                                                           </tr>
                                                           <tr>
                                                               <td colspan="2" style="padding-top: 25px;">
                                                                   <table border="0" cellpadding="0" cellspacing="0" width="100%%" bgcolor="#1a1a1a" style="background-color: #1a1a1a; background-image: url('data:image/svg+xml,%%3Csvg xmlns=&#39;http://www.w3.org/2000/svg&#39; width=&#39;1&#39; height=&#39;1&#39;%%3E%%3Crect width=&#39;1&#39; height=&#39;1&#39; fill=&#39;%%231a1a1a&#39;/%%3E%%3C/svg%%3E'); border-left: 3px solid #c5a059; border-radius: 4px;">
                                                                       <tr>
                                                                           <td style="padding: 15px; font-family: 'Inter', Helvetica, Arial, sans-serif;">
                                                                               <p style="margin: 0 0 5px 0; font-size: 12px; font-weight: 600; color: #c5a059; text-transform: uppercase; letter-spacing: 1px;">Notițe Client</p>
                                                                               <p style="margin: 0; font-size: 14px; color: #d0d0d0; line-height: 20px;">%s</p>
                                                                           </td>
                                                                       </tr>
                                                                   </table>
                                                               </td>
                                                           </tr>
                                                       </table>
                                                   </td>
                                               </tr>
                                           </table>
                                       </td>
                                   </tr>
                                   <tr>
                                       <td align="center" style="padding: 40px 20px 20px 20px;">
                                           <p style="margin: 0; font-family: 'Inter', Helvetica, Arial, sans-serif; font-size: 12px; color: #666666; line-height: 18px;">
                                               Acest email a fost generat automat de platforma Hype Barbershop.<br> Nu răspunde la acest mesaj.
                                           </p>
                                       </td>
                                   </tr>
                               </table>
                           </td>
                       </tr>
                   </table>
               </body>
               </html>
               """.formatted(
                dto.getBarberName(),
                dto.getClientName(),
                dto.getPhoneNumber(), dto.getPhoneNumber(),
                dto.getServiceName(),
                date,
                time,
                dto.getDuration(),
                info
        );
    }
}