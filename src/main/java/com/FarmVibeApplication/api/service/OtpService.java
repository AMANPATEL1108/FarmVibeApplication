package com.FarmVibeApplication.api.service;

import com.FarmVibeApplication.api.config.TwilioConfig;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private TwilioConfig twilioConfig;

    private Map<String, String> otpStorage = new HashMap<>();

    @PostConstruct
    public void init() {
        Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());
    }

    public String generateOtp(String phoneNumber) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStorage.put(phoneNumber, otp);

        Message.creator(
                new PhoneNumber(phoneNumber),
                new PhoneNumber(twilioConfig.getPhoneNumber()),
                "Your OTP is: " + otp
        ).create();

        return otp;
    }

    public boolean validateOtp(String phoneNumber, String otp) {
        String storedOtp = otpStorage.get(phoneNumber);
        if (storedOtp != null && storedOtp.equals(otp)) {
            otpStorage.remove(phoneNumber);
            return true;
        }
        return false;
    }
}
