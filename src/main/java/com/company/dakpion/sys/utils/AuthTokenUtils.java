package com.company.dakpion.sys.utils;

import com.company.dakpion.sys.entity.AppUser;
import com.company.dakpion.sys.entity.RefreshTokenEntity;
import com.company.dakpion.sys.model.CustomUserDetails;
import com.company.dakpion.sys.repository.AppUserRepo;
import com.company.dakpion.sys.repository.RefreshTokenRepo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Component
public class AuthTokenUtils {

    private static final int REFRESH_DAYS = 30;

    @Value("${app.jwt-secret}")
    private String jwtSecretKey;

    @Autowired
    private AppUserRepo appUserRepo;

    @Autowired(required = false)
    private RefreshTokenRepo refreshTokenRepo;

    @Value("${app-jwt-expiration-milliseconds:7200000}")
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
                .claim("referenceId", userPrincipal.getReferenceId())
                .claim("userTypeId", userPrincipal.getUserTypeId())
                .claim("displayName", userPrincipal.getUsername());

        Logger logger = Logger.getLogger(AuthTokenUtils.class.getName());
        logger.info("User ID: " + userPrincipal.getId() + " REF: " + userPrincipal.getReferenceId() + " UserType: " + userPrincipal.getUserTypeId());

        return builder
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(key())
                .compact();
    }

    public String generateAccessToken(AppUser user) {
        Date now    = new Date();
        Date expiry = new Date(now.getTime() + expirationTime);
        return Jwts.builder()
                .setSubject(user.getUsername() != null ? user.getUsername() : String.valueOf(user.getId()))
                .claim("id",          user.getId())
                .claim("role",        user.getAppUserType() != null ? user.getAppUserType().name() : "USER")
                .claim("referenceId", user.getId())
                .claim("userTypeId",  user.getUserTypeId() != null ? user.getUserTypeId() : 3)
                .claim("displayName", user.getDisplayName() != null ? user.getDisplayName() : user.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key())
                .compact();
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    // ── DB-backed refresh token helpers ───────────────────────────────────────

    public String createRefreshToken(Long userId, RefreshTokenRepo repo) {
        RefreshTokenRepo targetRepo = repo != null ? repo : this.refreshTokenRepo;
        String token = UUID.randomUUID().toString();
        if (targetRepo != null) {
            RefreshTokenEntity entity = RefreshTokenEntity.builder()
                    .token(token)
                    .userId(userId)
                    .expiresAt(LocalDateTime.now().plusDays(REFRESH_DAYS))
                    .createdAt(LocalDateTime.now())
                    .build();
            targetRepo.save(entity);
        }
        return token;
    }

    @Transactional
    public String[] validateAndRotateRefreshToken(String refreshToken, RefreshTokenRepo repo) {
        RefreshTokenRepo targetRepo = repo != null ? repo : this.refreshTokenRepo;
        if (targetRepo == null || !StringUtils.hasText(refreshToken)) {
            return null;
        }

        Optional<RefreshTokenEntity> tokenOpt = targetRepo.findByTokenAndExpiresAtAfter(refreshToken, LocalDateTime.now());
        if (tokenOpt.isEmpty()) {
            return null;
        }

        RefreshTokenEntity existing = tokenOpt.get();
        Long userId = existing.getUserId();

        // Rotate: delete old token, issue new token
        targetRepo.delete(existing);
        String newToken = createRefreshToken(userId, targetRepo);

        return new String[]{ newToken, String.valueOf(userId) };
    }

    @Transactional
    public void invalidateRefreshToken(String refreshToken, RefreshTokenRepo repo) {
        RefreshTokenRepo targetRepo = repo != null ? repo : this.refreshTokenRepo;
        if (targetRepo != null && StringUtils.hasText(refreshToken)) {
            targetRepo.deleteById(refreshToken);
        }
    }

    // ── JWT utility methods ───────────────────────────────────────────────────

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
        String token = getTokenFromRequest(request);
        if (!StringUtils.hasText(token)) {
            return null;
        }
        return getUserIdFromJwtToken(token);
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
        if (userTypeIdObj instanceof Long) {
            return (Long) userTypeIdObj;
        } else if (userTypeIdObj instanceof Integer) {
            return ((Integer) userTypeIdObj).longValue();
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

    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
