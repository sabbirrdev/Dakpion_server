package com.company.dakpion.security;

import com.company.dakpion.config.CurrentUserContext;
import com.company.dakpion.sys.model.CurrentUserInfo;
import com.company.dakpion.sys.services.CustomUserDetailsService;
import com.company.dakpion.sys.utils.AppUserType;
import com.company.dakpion.sys.utils.AuthTokenUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.logging.Logger;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private AuthTokenUtils authTokenUtils;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,@NonNull FilterChain filterChain) throws ServletException, IOException {
        final String requestUri = request.getRequestURI();
        final String method = request.getMethod();

        try {
            String token = authTokenUtils.getTokenFromRequest(request);
            if (token != null) {
                boolean isValid = authTokenUtils.validateJwtToken(token);
                if (isValid) {
                    // 🔐 Extract fields from JWT
                    String username = authTokenUtils.getUserNameFromJwtToken(token);
                    Long userId = authTokenUtils.getUserIdFromJwtToken(token);
                    AppUserType role = authTokenUtils.getRoleFromJwtToken(token);
                    Long referenceId = authTokenUtils.getReferenceIdFromJwtToken(token);
                    Long userTypeId = authTokenUtils.getUserTypeIdFromJwtToken(token);
                    String displayName = authTokenUtils.getDisplayNameFromJwtToken(token);

                    System.out.println("✅ [JWT FILTER SUCCESS] Request: " + method + " " + requestUri + " | User: " + username + " (ID: " + userId + ", Role: " + role + ")");

                    // 👤 Set Current User Context
                    CurrentUserInfo currentUser = new CurrentUserInfo(
                            userId,
                            username,
                            role,
                            referenceId,
                            userTypeId,
                            displayName
                    );
                    CurrentUserContext.set(currentUser);

                    // ✅ Spring Security authentication
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    System.out.println("⚠️ [JWT FILTER INVALID TOKEN] Request: " + method + " " + requestUri + " | Token provided but failed JWT validation!");
                }
            } else {
                System.out.println("ℹ️ [JWT FILTER NO TOKEN] Request: " + method + " " + requestUri + " | No Authorization header present");
            }

        } catch (Exception e) {
            System.out.println("❌ [JWT FILTER EXCEPTION] Request: " + method + " " + requestUri + " | Error: " + e.getMessage());
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
        // Clear after request completes (optional, for safety)
        CurrentUserContext.clear();
    }
}
