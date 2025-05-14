package services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SmsService {
    private static final String ACCOUNT_SID = "AC4211756b926c6275e1c75200efa94fae";
    private static final String AUTH_TOKEN = "2a5dee3de97cf7d1416591187b23daa2";
    private static final String FROM_PHONE_NUMBER = "+12792372079";

    static {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public void sendVerificationCode(String toPhoneNumber, String verificationCode) {
        Message message = Message.creator(
                        new PhoneNumber(toPhoneNumber),
                        new PhoneNumber(FROM_PHONE_NUMBER),
                        "Your verification code is: " + verificationCode)
                .create();
    }
}