package com.company.efood.security;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Only process if SecurityContext has not already been populated by JwtAuthFilter
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            String bearerToken = extractBearerToken(request);

            if (StringUtils.hasText(bearerToken) && !FirebaseApp.getApps().isEmpty()) {
                try {
                    // Cryptographically verify token with Firebase public keys
                    FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(bearerToken);

                    String uid = decodedToken.getUid();
                    String email = decodedToken.getEmail();
                    String phone = (String) decodedToken.getClaims().get("phone_number");
                    String name = decodedToken.getName();
                    String picture = decodedToken.getPicture();
                    boolean emailVerified = decodedToken.isEmailVerified();

                    List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

                    FirebaseUserDetails principal = FirebaseUserDetails.builder()
                            .uid(uid)
                            .email(email)
                            .phoneNumber(phone)
                            .name(name)
                            .picture(picture)
                            .emailVerified(emailVerified)
                            .authorities(authorities)
                            .build();

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("✅ [FIREBASE AUTH SUCCESS] UID: {}, Email: {}, Phone: {}", uid, email, phone);
                } catch (FirebaseAuthException ex) {
                    // Not a valid Firebase token; could be an internal backend JWT or invalid token.
                    // Allow filter chain to proceed to JwtAuthFilter.
                    log.debug("ℹ️ [FIREBASE AUTH] Token is not a valid Firebase ID token: {}", ex.getMessage());
                } catch (Exception ex) {
                    log.warn("⚠️ [FIREBASE AUTH] Unexpected error verifying token: {}", ex.getMessage());
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractBearerToken(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7).trim();
        }
        return null;
    }
}
