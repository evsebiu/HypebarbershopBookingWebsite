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
               <!DOCTYPE html>
                                                      <html lang="ro" xmlns="http://www.w3.org/1999/xhtml" xmlns:v="urn:schemas-microsoft-com:vml" xmlns:o="urn:schemas-microsoft-com:office:office">
                                                      <head>
                                                          <meta charset="utf-8">
                                                          <meta name="viewport" content="width=device-width, initial-scale=1.0">
                                                          <meta http-equiv="X-UA-Compatible" content="IE=edge">
                
                                                          <meta name="color-scheme" content="light dark">
                                                          <meta name="supported-color-schemes" content="light dark">
                
                                                          <title>Confirmare Programare - Hype Barbershop</title>
                
                                                          <link rel="preconnect" href="https://fonts.googleapis.com">
                                                          <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
                                                          <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600&family=Playfair+Display:ital,wght@0,600;1,600&display=swap" rel="stylesheet">
                
                                                          <style type="text/css">
                                                              :root { color-scheme: light dark; supported-color-schemes: light dark; }
                
                                                              body, table, td, a { -webkit-text-size-adjust: 100%%; -ms-text-size-adjust: 100%%; }
                                                              table, td { mso-table-lspace: 0pt; mso-table-rspace: 0pt; }
                                                              img { -ms-interpolation-mode: bicubic; border: 0; height: auto; line-height: 100%%; outline: none; text-decoration: none; }
                                                              body { height: 100%% !important; margin: 0 !important; padding: 0 !important; width: 100%% !important; background-color: #0a0a0a !important; }
                
                                                              .btn-cta:hover { background-color: #e5c352 !important; transform: translateY(-2px); }
                                                              .text-link:hover { color: #c5a059 !important; }
                
                                                              /* Apple Mail Gradient Hack - Fortam sa nu poata inversa cardul */
                                                              .apple-mail-card-fix {
                                                                  background-image: linear-gradient(#141414, #141414) !important;
                                                                  background-color: #141414 !important;
                                                              }
                
                                                              @media screen and (max-width: 600px) {
                                                                  .container { width: 100%% !important; padding: 0 15px !important; }
                                                                  .hero-title { font-size: 32px !important; line-height: 40px !important; }
                                                                  .card-wrapper { padding: 20px !important; }
                                                                  .receipt-row td { display: block !important; width: 100%% !important; text-align: left !important; }
                                                                  .receipt-value { padding-top: 5px !important; padding-bottom: 15px !important; }
                                                              }
                                                          </style>
                                                      </head>
                                                      <body style="background-color: #0a0a0a; margin: 0; padding: 0; -webkit-font-smoothing: antialiased;">
                                                          <table border="0" cellpadding="0" cellspacing="0" width="100%%" style="background-color: #0a0a0a; background-image: radial-gradient(circle at top, #1a1a1a 0%%, #0a0a0a 100%%);">
                                                              <tr>
                                                                  <td align="center" style="padding: 40px 0;">
                                                                      <table border="0" cellpadding="0" cellspacing="0" width="600" class="container" style="background-color: transparent;">
                                                                          <tr>
                                                                              <td align="center" style="padding-bottom: 40px;">
                                                                                  <h2 style="margin: 0; font-family: 'Playfair Display', Georgia, serif; font-size: 24px; font-weight: 600; color: #c5a059; letter-spacing: 2px; text-transform: uppercase;">
                                                                                      HYPE <span style="color: #f5f5f7;">BARBERSHOP</span>
                                                                                  </h2>
                                                                                  <div style="width: 40px; height: 1px; background-color: #c5a059; margin-top: 15px;"></div>
                                                                              </td>
                                                                          </tr>
                                                                          <tr>
                                                                              <td align="center" style="padding: 0 20px 30px 20px;">
                                                                                  <h1 class="hero-title" style="margin: 0; font-family: 'Playfair Display', Georgia, serif; font-size: 42px; font-weight: 600; color: #ffffff; line-height: 48px; margin-bottom: 15px;">
                                                                                      Programare Reușită.
                                                                                  </h1>
                                                                                  <p style="margin: 0; font-family: 'Inter', Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 300; color: #a0a0a0; line-height: 24px;">
                                                                                      Salut <strong style="color: #ffffff;">%s</strong>, te așteptăm cu plăcere la salon. Mai jos regăsești detaliile vizitei tale.
                                                                                  </p>
                                                                              </td>
                                                                          </tr>
                                                                          <tr>
                                                                              <td align="center" style="padding: 0 20px;">
                
                                                                                  <table border="0" cellpadding="0" cellspacing="0" width="100%%" class="card-wrapper apple-mail-card-fix" style="background-image: linear-gradient(#141414, #141414); border: 1px solid #2a2a2a; border-radius: 12px; border-top: 3px solid #c5a059;">
                                                                                      <tr>
                                                                                          <td style="padding: 30px;">
                                                                                              <table border="0" cellpadding="0" cellspacing="0" width="100%%">
                                                                                                  <tr>
                                                                                                      <td align="left" style="padding-bottom: 25px; border-bottom: 1px solid #2a2a2a;">
                                                                                                          <p style="margin: 0; font-family: 'Inter', Helvetica, Arial, sans-serif; font-size: 12px; font-weight: 600; color: #c5a059; letter-spacing: 2px; text-transform: uppercase;">
                                                                                                              Detalii Rezervare
                                                                                                          </p>
                                                                                                      </td>
                                                                                                  </tr>
                                                                                              </table>
                                                                                              <table border="0" cellpadding="0" cellspacing="0" width="100%%" style="margin-top: 25px;">
                                                                                                  <tr class="receipt-row">
                                                                                                      <td align="left" style="padding-bottom: 15px; font-family: 'Inter', Helvetica, Arial, sans-serif; font-size: 14px; color: #a0a0a0;">Serviciu</td>
                                                                                                      <td align="right" class="receipt-value" style="padding-bottom: 15px; font-family: 'Inter', Helvetica, Arial, sans-serif; font-size: 15px; font-weight: 600; color: #f5f5f7;">%s</td>
                                                                                                  </tr>
                                                                                                  <tr class="receipt-row">
                                                                                                      <td align="left" style="padding-bottom: 15px; font-family: 'Inter', Helvetica, Arial, sans-serif; font-size: 14px; color: #a0a0a0;">Specialist</td>
                                                                                                      <td align="right" class="receipt-value" style="padding-bottom: 15px; font-family: 'Inter', Helvetica, Arial, sans-serif; font-size: 15px; font-weight: 600; color: #f5f5f7;">%s</td>
                                                                                                  </tr>
                                                                                                  <tr class="receipt-row">
                                                                                                      <td align="left" style="padding-bottom: 15px; font-family: 'Inter', Helvetica, Arial, sans-serif; font-size: 14px; color: #a0a0a0;">Dată</td>
                                                                                                      <td align="right" class="receipt-value" style="padding-bottom: 15px; font-family: 'Inter', Helvetica, Arial, sans-serif; font-size: 15px; font-weight: 600; color: #f5f5f7;">%s</td>
                                                                                                  </tr>
                                                                                                  <tr class="receipt-row">
                                                                                                      <td align="left" style="padding-bottom: 25px; font-family: 'Inter', Helvetica, Arial, sans-serif; font-size: 14px; color: #a0a0a0;">Ora</td>
                                                                                                      <td align="right" class="receipt-value" style="padding-bottom: 25px; font-family: 'Inter', Helvetica, Arial, sans-serif; font-size: 15px; font-weight: 600; color: #c5a059;">%s</td>
                                                                                                  </tr>
                                                                                                  <tr>
                                                                                                      <td colspan="2" style="border-top: 1px solid #2a2a2a; padding-top: 25px;">
                                                                                                          <table border="0" cellpadding="0" cellspacing="0" width="100%%">
                                                                                                              <tr>
                                                                                                                  <td align="left" style="font-family: 'Inter', Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 600; color: #ffffff;">Total</td>
                                                                                                                  <td align="right" style="font-family: 'Playfair Display', Georgia, serif; font-size: 24px; font-weight: 600; color: #c5a059;">%.2f RON</td>
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
                                                                              <td align="center" style="padding: 40px 20px;">
                                                                                  <p style="margin: 0 0 25px 0; font-family: 'Inter', Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 300; color: #a0a0a0; line-height: 22px;">
                                                                                      Te rugăm să ajungi cu <strong style="color: #ffffff;">5 minute înainte</strong> de ora stabilită.<br> Pentru modificări, contactează-ne telefonic.
                                                                                  </p>
                                                                                  <table border="0" cellpadding="0" cellspacing="0">
                                                                                      <tr>
                                                                                          <td align="center" style="background-image: linear-gradient(#c5a059, #c5a059); background-color: #c5a059; border-radius: 50px;">
                                                                                              <a href="tel:+40741516583" class="btn-cta" style="display: inline-block; padding: 16px 40px; font-family: 'Inter', Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 700; color: #0a0a0a; text-decoration: none; text-transform: uppercase; letter-spacing: 1px;">Sună la Salon</a>
                                                                                          </td>
                                                                                      </tr>
                                                                                  </table>
                                                                              </td>
                                                                          </tr>
                                                                          <tr>
                                                                              <td align="center" style="padding: 30px 20px; border-top: 1px solid #1a1a1a;">
                                                                                  <p style="margin: 0; font-family: 'Inter', Helvetica, Arial, sans-serif; font-size: 12px; color: #666666; line-height: 18px;">
                                                                                      &copy; 2024 Hype Barbershop. Toate drepturile rezervate.<br> Ai primit acest email deoarece ai făcut o programare pe site-ul nostru.
                                                                                  </p>
                                                                              </td>
                                                                          </tr>
                                                                      </table>
                                                                  </td>
                                                              </tr>
                                                          </table>
                                                      </body>
                                                      </html>
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
               <!DOCTYPE html>
                                              <html lang="ro" xmlns="http://www.w3.org/1999/xhtml" xmlns:v="urn:schemas-microsoft-com:vml" xmlns:o="urn:schemas-microsoft-com:office:office">
                                              <head>
                                                  <meta charset="utf-8">
                                                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                                                  <meta http-equiv="X-UA-Compatible" content="IE=edge">
                                                  <title>Programare Nouă - Hype Barbershop</title>
                                                  <link rel="preconnect" href="https://fonts.googleapis.com">
                                                  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
                                                  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600&family=Playfair+Display:ital,wght@0,600;1,600&display=swap" rel="stylesheet">
                                                  <style type="text/css">
                                                      body, table, td, a { -webkit-text-size-adjust: 100%%; -ms-text-size-adjust: 100%%; }
                                                      table, td { mso-table-lspace: 0pt; mso-table-rspace: 0pt; }
                                                      img { -ms-interpolation-mode: bicubic; border: 0; height: auto; line-height: 100%%; outline: none; text-decoration: none; }
                                                      body { height: 100%% !important; margin: 0 !important; padding: 0 !important; width: 100%% !important; background-color: #0a0a0a; }
                                                      .text-link:hover { color: #e5c352 !important; }
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
                                                  <table border="0" cellpadding="0" cellspacing="0" width="100%%" style="background-color: #0a0a0a; background-image: radial-gradient(circle at top, #1a1a1a 0%%, #0a0a0a 100%%);">
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
                                                                              Salut <strong>%s</strong>,<br> O nouă rezervare a fost adăugată în calendarul tău.
                                                                          </p>
                                                                      </td>
                                                                  </tr>
                                                                  <tr>
                                                                      <td align="center" style="padding: 0 20px;">
                                                                          <table border="0" cellpadding="0" cellspacing="0" width="100%%" class="card-wrapper" style="background-color: #141414; border: 1px solid #2a2a2a; border-radius: 12px; border-top: 3px solid #c5a059;">
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
                                                                                                  <a href="tel:%s" class="text-link" style="color: #c5a059; text-decoration: none;">%s</a>
                                                                                              </td>
                                                                                          </tr>
                                                                                          <tr class="receipt-row">
                                                                                              <td align="left" style="padding-bottom: 15px; font-family: 'Inter', Helvetica, Arial, sans-serif; font-size: 14px; color: #a0a0a0;">Serviciu</td>
                                                                                              <td align="right" class="receipt-value" style="padding-bottom: 15px; font-family: 'Inter', Helvetica, Arial, sans-serif; font-size: 15px; font-weight: 600; color: #f5f5f7;">%s</td>
                                                                                          </tr>
                                                                                          <tr class="receipt-row">
                                                                                              <td align="left" style="padding-bottom: 15px; font-family: 'Inter', Helvetica, Arial, sans-serif; font-size: 14px; color: #a0a0a0;">Dată</td>
                                                                                              <td align="right" class="receipt-value" style="padding-bottom: 15px; font-family: 'Inter', Helvetica, Arial, sans-serif; font-size: 15px; font-weight: 600; color: #f5f5f7;">%s</td>
                                                                                          </tr>
                                                                                          <tr class="receipt-row">
                                                                                              <td align="left" style="padding-bottom: 15px; font-family: 'Inter', Helvetica, Arial, sans-serif; font-size: 14px; color: #a0a0a0;">Ora</td>
                                                                                              <td align="right" class="receipt-value" style="padding-bottom: 15px; font-family: 'Inter', Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 600; color: #c5a059;">%s</td>
                                                                                          </tr>
                                                                                          <tr class="receipt-row">
                                                                                              <td align="left" style="padding-bottom: 25px; border-bottom: 1px solid #2a2a2a; font-family: 'Inter', Helvetica, Arial, sans-serif; font-size: 14px; color: #a0a0a0;">Durată est.</td>
                                                                                              <td align="right" class="receipt-value" style="padding-bottom: 25px; border-bottom: 1px solid #2a2a2a; font-family: 'Inter', Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 600; color: #f5f5f7;">%d min</td>
                                                                                          </tr>
                                                                                          <tr>
                                                                                              <td colspan="2" style="padding-top: 25px;">
                                                                                                  <table border="0" cellpadding="0" cellspacing="0" width="100%%" style="background-color: #1a1a1a; border-left: 3px solid #c5a059; border-radius: 4px;">
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
                dto.getPhoneNumber(), dto.getPhoneNumber(), // Pus de doua ori pentru a face link-ul apelabil pe telefon
                dto.getServiceName(),
                date,
                time,
                dto.getDuration(),
                info
        );
    }
}
