package com.company.efood.sys.utils;

import com.company.efood.sys.dto.RefreshTokenDto;
import com.company.efood.sys.entity.AppUser;
import com.company.efood.sys.model.CustomUserDetails;
import com.company.efood.sys.repository.AppUserRepo;
import com.company.efood.sys.services.RefreshTokenService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

@Component
public class AuthTokenUtils {
    @Value("${app.jwt-secret}")
    private String jwtSecretKey;

    @Autowired
    private AppUserRepo appUserRepo;
    @Autowired
    private RefreshTokenService refreshTokenService;

    @Value("${app-jwt-expiration-milliseconds}")
    private Long expirationTime;

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecretKey));
    }

    public String generateJWTToken(Authentication authentication) {
        CustomUserDetails userPrincipal = (CustomUserDetails) authentication.getPrincipal();
        Date expireDate = new Date(new Date().getTime() + expirationTime);
        JwtBuilder builder = Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .claim("id", userPrincipal.getId())
                .claim("role", userPrincipal.getAppUserType())
                .claim("referenceId",userPrincipal.getReferenceId())
                .claim("userTypeId", userPrincipal.getUserTypeId())  // add this
                .claim("displayName", userPrincipal.getUsername()) ;

        Logger logger  = Logger.getLogger(AuthTokenUtils.class.getName());
        logger.info("User ID: " + userPrincipal.getId() + "REF: " + userPrincipal.getReferenceId() + "UserType: " + userPrincipal.getUserTypeId());

        // or get displayName from userPrincipal
        return builder
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(key())
                .compact();
    }

    public RefreshTokenDto generateRefreshToken(String userName) {
        AppUser appUser = appUserRepo.findByUsername(userName).orElseThrow(
                () -> new RuntimeException("User not found")
        );
        boolean isPresent = refreshTokenService.isRefreshTokenPresent(appUser.getId());
        System.out.println(isPresent);

        RefreshTokenDto refreshTokenDto = new RefreshTokenDto();
        refreshTokenDto.setRefreshToken(UUID.randomUUID().toString());
        refreshTokenDto.setExpireTime(Instant.now().plusMillis(60 * 60 * 1000));
        refreshTokenDto.setAppUserId(appUser.getId());
        if (isPresent) {
            System.out.println("on update");
            return refreshTokenService.update(refreshTokenDto, appUser.getId());
        } else {
            System.out.println("on insert");
            return refreshTokenService.save(refreshTokenDto, appUser.getId());
        }
    }

    public boolean verifyRefreshToken(String refreshToken) {
        return refreshTokenService.findByToken(refreshToken).isPresent();
    }
    public RefreshTokenDto getRefreshToken(String refreshToken) {
        return refreshTokenService.findByToken(refreshToken).orElseThrow(
                () -> new RuntimeException("Refresh token not found")
        );
    }
    public String getUsernameById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }
        return appUserRepo.findById(id).orElseThrow(
                () -> new RuntimeException("User not found with id: " + id)
        ).getUsername();
    }


    public String getTokenFromRequest(HttpServletRequest request) {
        String token = null;
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            token = headerAuth.substring(7);
        }
        return token;
    }
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parse(authToken);
            return true;
        } catch (SignatureException e) {
            System.out.println("Invalid JWT SIGNATURE " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("Invalid jwt token" + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.out.println("Jwt token is expired" + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("Jwt token is unsupported" + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Jwt claims string is empty" + e.getMessage());
        }
        return false;
    }
    public Long getUserIdFromJwtToken(String token) {
        Object userId = Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody().get("id");
        return userId != null ? Long.valueOf(userId.toString()) : null;
    }
    public Long getUserIdFromRequest(HttpServletRequest request) {
        return Long.valueOf(getUserIdFromJwtToken(getTokenFromRequest(request)));
    }
    public String getUserNameFromJwtToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public AppUserType getRoleFromJwtToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody();
        return AppUserType.valueOf(String.valueOf(claims.get("role")));
    }
    public AppUserType getRoleFromRequest(HttpServletRequest request) {
        return getRoleFromJwtToken(getTokenFromRequest(request));
    }
    public Long getReferenceIdFromJwtToken(String token) {
        Object refId = Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody().get("referenceId");
        return refId != null ? Long.valueOf(refId.toString()) : null;
    }
    public Long getReferenceIdFromRequest(HttpServletRequest request) {
        return getReferenceIdFromJwtToken(getTokenFromRequest(request));
    }

    public Long getUserTypeIdFromJwtToken(String token) {
        Claims claims = getAllClaims(token);
        Object userTypeIdObj = claims.get("userTypeId");
        if (userTypeIdObj == null) {
            return null;
        }
        // The claim value might be Integer or String, so handle accordingly
        if (userTypeIdObj instanceof Long) {
            return (Long) userTypeIdObj;
        } else if (userTypeIdObj instanceof String) {
            try {
                return Long.parseLong((String) userTypeIdObj);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public String getDisplayNameFromJwtToken(String token) {
        Claims claims = getAllClaims(token);
        Object displayNameObj = claims.get("displayName");
        if (displayNameObj != null) {
            return displayNameObj.toString();
        }
        return null;
    }

    // Helper method to get all claims from the token
    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


}
