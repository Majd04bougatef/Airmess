package util;

import models.Offre;
import models.Reservation;
import models.Users;
import services.UsersService;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class Mailer {
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587"; // Use 465 for SSL
    private static final String EMAIL = "oussemabelhajbb22@gmail.com";
    private static final String PASSWORD = "vtah mvgq gpra jzgw"; // Use App Password for Gmail
    private static final String COMPANY_WEBSITE = "https://yourcompany.com/contact";

    public static void sendMessage(String recipientEmail, String subject, String messageBody) {
        // Configure SMTP server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true"); // Enable TLS
        properties.put("mail.smtp.host", SMTP_HOST);
        properties.put("mail.smtp.port", SMTP_PORT);
        properties.put("mail.smtp.ssl.trust", SMTP_HOST); // Trust the SMTP server

        // Create a session with an authenticator
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL, PASSWORD);
            }
        });
        session.setDebug(true); // Enable debugging

        // Create the email message
        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(EMAIL));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject(subject);
            message.setText(messageBody);

            // Send the email
            Transport.send(message);
            System.out.println("Email sent successfully to " + recipientEmail);
        } catch (MessagingException e) {
            System.err.println("Error sending email: " + e.getMessage());
        }
    }

    public static void sendReservationConfirmation(String recipientEmail, Reservation reservation, Offre offre) {
        try {
            // Get user information
            UsersService userService = new UsersService();
            Users user = userService.getUserByEmail(recipientEmail);
            String userName = user != null ? user.getName() + " " + user.getPrenom() : "Valued Customer";

            // Format dates
            DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
            String startDate = offre.getStartDate().split(" ")[0];
            String endDate = offre.getEndDate().split(" ")[0];
            String formattedStartDate = LocalDate.parse(startDate).format(displayFormatter);
            String formattedEndDate = LocalDate.parse(endDate).format(displayFormatter);

            // Get HTML template
            String htmlTemplate = getReservationTemplate();

            // Replace placeholders with actual values
            htmlTemplate = htmlTemplate.replace("{{NAME}}", userName)
                    .replace("{{RESERVATION_ID}}", String.valueOf(reservation.getIdR()))
                    .replace("{{RESERVATION_DATE}}", reservation.getDateRes())
                    .replace("{{PAYMENT_METHOD}}", getPaymentMethodDisplayName(reservation.getModePaiement()))
                    .replace("{{DESCRIPTION}}", offre.getDescription())
                    .replace("{{PLACE}}", getFormattedPlace(offre.getPlace()))
                    .replace("{{START_DATE}}", formattedStartDate)
                    .replace("{{END_DATE}}", formattedEndDate)
                    .replace("{{PRICE}}", String.format("%.2f", offre.getPriceAfter()))
                    .replace("{{CONTACT_URL}}", COMPANY_WEBSITE);

            // Handle image
            boolean hasImage = offre.getImage() != null && !offre.getImage().isEmpty();
            if (hasImage) {
                htmlTemplate = htmlTemplate.replace("{{#if IMAGE_URL}}", "")
                        .replace("{{IMAGE_URL}}", "cid:eventImage")
                        .replace("{{/if}}", "");
            } else {
                // Remove image section if no image
                htmlTemplate = htmlTemplate.replaceAll("\\{\\{#if IMAGE_URL\\}}.*?\\{\\{/if\\}}", "");
            }

            // Create multipart email
            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host", SMTP_HOST);
            properties.put("mail.smtp.port", SMTP_PORT);
            properties.put("mail.smtp.ssl.trust", SMTP_HOST);

            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL, PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject("Reservation Confirmation");

            // Set up multipart message
            Multipart multipart = new MimeMultipart();

            // HTML part
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlTemplate, "text/html; charset=utf-8");
            multipart.addBodyPart(htmlPart);

            // Add image as attachment if exists
            if (hasImage) {
                File imageFile = new File(offre.getImage());
                if (imageFile.exists()) {
                    MimeBodyPart imagePart = new MimeBodyPart();
                    DataSource source = new FileDataSource(imageFile);
                    imagePart.setDataHandler(new DataHandler(source));
                    imagePart.setHeader("Content-ID", "<eventImage>");
                    multipart.addBodyPart(imagePart);
                }
            }

            message.setContent(multipart);
            Transport.send(message);
            System.out.println("Reservation confirmation email sent to " + recipientEmail);

        } catch (Exception e) {
            System.err.println("Error sending reservation email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String getPaymentMethodDisplayName(String method) {
        return switch (method.toLowerCase()) {
            case "carte" -> "Credit Card";
            case "espece" -> "Cash";
            default -> method;
        };
    }

    private static String getFormattedPlace(String place) {
        try {
            String lat = place.split(",")[0].trim();
            String lon = place.split(",")[1].trim();
            return GeoCodeApi.getAddressFromLatLong(lat, lon);
        } catch (Exception e) {
            return place;
        }
    }

    private static String getReservationTemplate() {
        // This would typically load from a file, but for simplicity,
        // we return the template as a string
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Reservation Confirmation</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: 'Arial', sans-serif;\n" +
                "            line-height: 1.6;\n" +
                "            color: #333333;\n" +
                "            max-width: 600px;\n" +
                "            margin: 0 auto;\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "        .header {\n" +
                "            background-color: #4CAF50;\n" +
                "            color: white;\n" +
                "            padding: 20px;\n" +
                "            text-align: center;\n" +
                "            border-radius: 5px 5px 0 0;\n" +
                "        }\n" +
                "        .content {\n" +
                "            background-color: #f9f9f9;\n" +
                "            padding: 20px;\n" +
                "            border-left: 1px solid #dddddd;\n" +
                "            border-right: 1px solid #dddddd;\n" +
                "        }\n" +
                "        .footer {\n" +
                "            background-color: #eeeeee;\n" +
                "            padding: 15px;\n" +
                "            text-align: center;\n" +
                "            font-size: 12px;\n" +
                "            border-radius: 0 0 5px 5px;\n" +
                "            border: 1px solid #dddddd;\n" +
                "        }\n" +
                "        .offer-image {\n" +
                "            width: 100%;\n" +
                "            max-height: 300px;\n" +
                "            object-fit: cover;\n" +
                "            margin: 15px 0;\n" +
                "            border-radius: 5px;\n" +
                "        }\n" +
                "        .details {\n" +
                "            background-color: #ffffff;\n" +
                "            padding: 15px;\n" +
                "            border-radius: 5px;\n" +
                "            margin: 15px 0;\n" +
                "            border: 1px solid #dddddd;\n" +
                "        }\n" +
                "        .button {\n" +
                "            display: inline-block;\n" +
                "            background-color: #4CAF50;\n" +
                "            color: white;\n" +
                "            padding: 10px 20px;\n" +
                "            text-decoration: none;\n" +
                "            border-radius: 5px;\n" +
                "            margin-top: 15px;\n" +
                "        }\n" +
                "        .price {\n" +
                "            font-size: 18px;\n" +
                "            font-weight: bold;\n" +
                "            color: #4CAF50;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"header\">\n" +
                "        <h1>Reservation Confirmation</h1>\n" +
                "    </div>\n" +
                "    <div class=\"content\">\n" +
                "        <p>Dear {{NAME}},</p>\n" +
                "        <p>Thank you for your reservation. We are pleased to confirm your booking!</p>\n" +
                "        \n" +
                "        <div class=\"details\">\n" +
                "            <h2>Reservation Details</h2>\n" +
                "            <p><strong>Reservation ID:</strong> {{RESERVATION_ID}}</p>\n" +
                "            <p><strong>Date:</strong> {{RESERVATION_DATE}}</p>\n" +
                "            <p><strong>Payment Method:</strong> {{PAYMENT_METHOD}}</p>\n" +
                "        </div>\n" +
                "        \n" +
                "        <div class=\"details\">\n" +
                "            <h2>Event Details</h2>\n" +
                "            <p><strong>Description:</strong> {{DESCRIPTION}}</p>\n" +
                "            <p><strong>Location:</strong> {{PLACE}}</p>\n" +
                "            <p><strong>Event Period:</strong> {{START_DATE}} to {{END_DATE}}</p>\n" +
                "            <p class=\"price\">Price: {{PRICE}}</p>\n" +
                "        </div>\n" +
                "        \n" +
                "        {{#if IMAGE_URL}}\n" +
                "        <img src=\"{{IMAGE_URL}}\" alt=\"Event Image\" class=\"offer-image\">\n" +
                "        {{/if}}\n" +
                "        \n" +
                "        <p>If you have any questions or need to make changes to your reservation, please contact our customer service.</p>\n" +
                "        \n" +
                "        <a href=\"{{CONTACT_URL}}\" class=\"button\">Contact Us</a>\n" +
                "    </div>\n" +
                "    <div class=\"footer\">\n" +
                "        <p>© 2024 Your Company Name. All rights reserved.</p>\n" +
                "        <p>This is an automated email, please do not reply directly to this message.</p>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
}