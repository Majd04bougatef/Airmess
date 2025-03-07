package services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SmsService {
    private static final String ACCOUNT_SID = "AC3d165128a264ab851cadb626e2c8240e";
    private static final String AUTH_TOKEN = "458a1c25d2f2a1162639dc9e506c829b";
    private static final String FROM_PHONE_NUMBER = "+16812011794";

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