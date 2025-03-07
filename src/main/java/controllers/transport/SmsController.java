package controllers.transport;

import services.SmsService;

import javax.servlet.http.HttpSession;

public class SmsController {

    private String generatedCode;

    public void sendSms(HttpSession session, String phoneNumber) {
        // Generate a 4-digit random code
        generatedCode = String.valueOf((int) (Math.random() * 9000) + 1000);

        // Store the code in the session
        session.setAttribute("generatedCode", generatedCode);

        // Send the SMS using your SmsService
        SmsService smsService = new SmsService();
        smsService.sendVerificationCode(phoneNumber, generatedCode);

    }
}