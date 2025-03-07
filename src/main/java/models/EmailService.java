package models;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailService {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587"; // Use 465 for SSL
    private static final String EMAIL = "oussemabelhajbb22@gmail.com";
    private static final String PASSWORD = "vtah mvgq gpra jzgw"; // Use App Password for Gmail

    public static void sendConfirmationEmail(String recipientEmail, String subject, String verificationCode) throws MessagingException {
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
        message.setFrom(new InternetAddress(EMAIL));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
        message.setSubject(subject);

        // HTML email content
        String htmlContent = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Verify Your Code</title>\n" +
                "</head>\n" +
                "<body style=\"margin: 0; padding: 0; font-family: Arial, sans-serif; background: linear-gradient(135deg, #87CEEB, #FFC0CB);\">\n" +
                "    <div style=\"max-width: 600px; margin: 40px auto; background-color: white; border-radius: 20px; box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1); overflow: hidden;\">\n" +
                "        <div style=\"padding: 20px; text-align: left;\">\n" +
                "            <div style=\"height: 50px; margin-bottom: 10px;\">\n" +
                "                <span style=\"display: inline-block; background-color: #ffd0c2; width: 40px; height: 40px; border-radius: 10px; text-align: center; line-height: 40px; font-weight: bold; color: #3a6c99; font-size: 20px;\">A</span>\n" +
                "                <span style=\"color: #3a6c99; font-weight: bold; font-size: 20px; vertical-align: middle; margin-left: 5px;\">AirMess</span>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        <div style=\"padding: 20px 40px 40px;\">\n" +
                "            <h1 style=\"color: #3a6c99; font-size: 24px; margin-bottom: 20px;\">Vérifiez votre code d'inscription</h1>\n" +
                "            <p style=\"color: #555; line-height: 1.5; margin-bottom: 20px;\">Bonjour,</p>\n" +
                "            <p style=\"color: #555; line-height: 1.5; margin-bottom: 20px;\">Merci de vérifier votre adresse email. Veuillez utiliser le code ci-dessous pour compléter votre inscription:</p>\n" +
                "            \n" +
                "            <div style=\"background-color: #f5f9ff; border-radius: 10px; padding: 20px; text-align: center; margin: 30px 0;\">\n" +
                "                <div style=\"font-size: 32px; font-weight: bold; letter-spacing: 5px; color: #3a6c99;\">" + verificationCode + "</div>\n" +
                "            </div>\n" +
                "            \n" +
                "            <p style=\"color: #555; line-height: 1.5; margin-bottom: 20px;\">Ce code expirera dans 2 minutes. Si vous n'avez pas demandé ce code, veuillez ignorer cet email.</p>\n" +
                "        </div>\n" +
                "        <div style=\"background-color: #f8f8f8; padding: 20px; text-align: center; font-size: 12px; color: #999;\">\n" +
                "            <div style=\"margin: 15px 0;\">\n" +
                "                <span style=\"display: inline-block; width: 30px; height: 30px; background-color: #e0e0e0; border-radius: 50%; margin: 0 5px; text-align: center; line-height: 30px;\">G</span>\n" +
                "                <span style=\"display: inline-block; width: 30px; height: 30px; background-color: #e0e0e0; border-radius: 50%; margin: 0 5px; text-align: center; line-height: 30px;\">f</span>\n" +
                "                <span style=\"display: inline-block; width: 30px; height: 30px; background-color: #e0e0e0; border-radius: 50%; margin: 0 5px; text-align: center; line-height: 30px;\">in</span>\n" +
                "            </div>\n" +
                "            <p style=\"margin-bottom: 10px;\">© 2025 AirMess. Tous droits réservés.</p>\n" +
                "            <p style=\"margin-bottom: 10px;\">Si vous avez des questions, contactez-nous à support@airmess.com</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";

        // Set HTML content
        message.setContent(htmlContent, "text/html");

        // Send the email
        Transport.send(message);
        System.out.println("Confirmation email sent successfully to " + recipientEmail);
    }
}