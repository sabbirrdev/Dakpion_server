package com.company.efood.sys.services;

public interface EmailOtpService {
    boolean sendOtpEmail(String email, String otp);
}
