package com.company.dakpion.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        final String authHeader = request.getHeader("Authorization");
        System.out.println("❌ [SPRING SECURITY 401] Unauthorized error on " + request.getMethod() + " " + request.getRequestURI() + " | Auth Header: " + (authHeader != null ? "PRESENT" : "MISSING") + " | Error: " + authException.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }

}
