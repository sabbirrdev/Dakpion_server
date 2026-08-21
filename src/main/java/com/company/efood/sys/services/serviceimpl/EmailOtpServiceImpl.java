package com.company.efood.sys.services.serviceimpl;

import com.company.efood.sys.services.EmailOtpService;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailOtpServiceImpl implements EmailOtpService {

    private final JavaMailSender javaMailSender;

    @Override
    public boolean sendOtpEmail(String email, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Your Email Verification OTP");
            message.setText("Your verification OTP is: " + otp + "\n\nThis code will expire in 5 minutes.");
            javaMailSender.send(message);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
