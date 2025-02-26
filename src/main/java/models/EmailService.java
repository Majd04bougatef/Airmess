
package models;

import javax.mail.*;
        import javax.mail.internet.*;
        import java.util.Properties;

public class EmailService {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587"; // Use 465 for SSL
    private static final String EMAIL = "oussemabelhajbb22@gmail.com";
    private static final String PASSWORD = "vtah mvgq gpra jzgw"; // Use App Password for Gmail

    public static void sendConfirmationEmail(String recipientEmail, String subject, String messageBody) throws MessagingException {
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
        message.setText(messageBody);

        // Send the email
        Transport.send(message);
        System.out.println("Confirmation email sent successfully to " + recipientEmail);
    }
}