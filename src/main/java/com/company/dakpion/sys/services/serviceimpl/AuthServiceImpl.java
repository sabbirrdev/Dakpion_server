package com.company.dakpion.sys.services.serviceimpl;

import com.company.dakpion.base.BaseUtils;
import com.company.dakpion.dakpion.dto.OtpRequestDto;
import com.company.dakpion.dakpion.dto.OtpResponseDto;
import com.company.dakpion.dakpion.dto.OtpVerifyRequestDto;
import com.company.dakpion.dakpion.dto.OtpVerifyResponseDto;
import com.company.dakpion.dakpion.repository.DakpionLetterRepo;
import com.company.dakpion.dakpion.service.DakpionOtpService;
import com.company.dakpion.sys.dto.AuthSessionResponseDto;
import com.company.dakpion.sys.dto.ForgotPasswordRequestDto;
import com.company.dakpion.sys.dto.RefreshTokenRequestDto;
import com.company.dakpion.sys.dto.ResetPasswordRequestDto;
import com.company.dakpion.sys.entity.AppUser;
import com.company.dakpion.sys.model.AuthResponseModel;
import com.company.dakpion.sys.model.CustomUserDetails;
import com.company.dakpion.sys.model.LoginRequestModel;
import com.company.dakpion.sys.model.RegisterRequestModel;
import com.company.dakpion.sys.repository.AppUserRepo;
import com.company.dakpion.sys.repository.RefreshTokenRepo;
import com.company.dakpion.sys.services.AuthService;
import com.company.dakpion.sys.utils.AppUserType;
import com.company.dakpion.sys.utils.AuthTokenUtils;
import com.company.dakpion.sys.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.company.dakpion.base.BaseConstants.*;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private  AuthTokenUtils authTokenUtils;
    @Autowired
    private  RefreshTokenRepo refreshTokenRepo;
    @Autowired
    private  AuthenticationManager authenticationManager;

    @Value("${app-jwt-expiration-milliseconds:7200000}")
    private long expiration;
    @Autowired
    private AppUserRepo appUserRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private BaseUtils baseUtils;
    @Autowired
    private DakpionOtpService dakpionOtpService;
    @Autowired
    private DakpionLetterRepo dakpionLetterRepo;

    @Override
    public AuthResponseModel signIn(LoginRequestModel loginRequestModel) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestModel.getUsername(), loginRequestModel.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String token = authTokenUtils.generateJWTToken(authentication);

        String refreshToken = authTokenUtils.createRefreshToken(userDetails.getId(), refreshTokenRepo);
        return new AuthResponseModel(
                token,
                refreshToken,
                userDetails.getUserTypeId(),
                userDetails.getAppUserType(),
                System.currentTimeMillis(),
                expiration
        );
    }

    @Override
    public AuthResponseModel signUp(RegisterRequestModel registerRequestModel) {
        if (registerRequestModel.getUsername() == null || registerRequestModel.getUsername().trim().isEmpty() ||
                registerRequestModel.getPassword() == null || registerRequestModel.getPassword().trim().isEmpty()) {
          throw new RuntimeException("Username and password cannot be empty");
        }
        if (appUserRepo.findByUsername(registerRequestModel.getUsername().trim()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

            AppUser savedUser = appUserRepo.save(generateAppUserEntity(registerRequestModel));

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(registerRequestModel.getUsername(), registerRequestModel.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = authTokenUtils.generateJWTToken(authentication);
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String refreshToken = authTokenUtils.createRefreshToken(savedUser.getId(), refreshTokenRepo);

           return  new AuthResponseModel(
                   token,
                   refreshToken,
                   userDetails.getUserTypeId(),
                   userDetails.getAppUserType(),
                   System.currentTimeMillis(),
                   expiration
           );
    }

    @Override
    public OtpResponseDto requestAuthOtp(Long userId) {
        return dakpionOtpService.requestOtp(userId);
    }

    @Override
    public AuthSessionResponseDto verifyAuthOtp(OtpVerifyRequestDto requestDto) {
        OtpVerifyResponseDto verifyResult = dakpionOtpService.verifyOtp(new OtpVerifyRequestDto(requestDto.getRequestId(), requestDto.getCode()));
        String phone = dakpionOtpService.getVerifiedPhone(verifyResult.getVerificationToken(), requestDto.getRequestId());
        if (!StringUtils.hasText(phone)) {
           throw  new RuntimeException("Could not resolve verified phone");
        }

        AppUser user = appUserRepo.findByPhone(phone).orElseGet(() -> {
            AppUser newUser = new AppUser();
            String generatedUsername = "user_" + phone.replaceAll("[^0-9]", "");
            String finalUsername = generatedUsername;
            int suffix = 1;
            while (appUserRepo.findByUsername(finalUsername).isPresent()) {
                finalUsername = generatedUsername + "_" + suffix++;
            }
            newUser.setUsername(finalUsername);
            newUser.setPhone(phone);
            newUser.setDisplayName("DakPion User");
            newUser.setAppUserType(AppUserType.USER);
            newUser.setUserTypeId(USER_TYPE_ID_USER);
            newUser.setPhoneVerified(true);
            newUser.setPassword(passwordEncoder.encode(java.util.UUID.randomUUID().toString()));
            newUser.setEntryDate(LocalDateTime.now());
            newUser.setEntryUser(0L);
            return appUserRepo.save(newUser);
        });

        if (Boolean.FALSE.equals(user.getPhoneVerified())) {
            user.setPhoneVerified(true);
            appUserRepo.save(user);
        }

        // Backfill letters
        try {
            dakpionLetterRepo.findAllByRecipientPhoneOrderByCreatedAtDesc(phone).forEach(letter -> {
                if (letter.getRecipientUserId() == null) {
                    letter.setRecipientUserId(user.getId());
                    dakpionLetterRepo.save(letter);
                }
            });
        } catch (Exception e) {
           // log.warn("Letter backfill failed for phone={}, continuing: {}", SecurityUtils.maskPhone(phone), e.getMessage());
        }

        String accessToken  = authTokenUtils.generateAccessToken(user);
        String refreshToken = authTokenUtils.createRefreshToken(user.getId(), refreshTokenRepo);
        String maskedPhone  = SecurityUtils.maskPhone(phone);

        return AuthSessionResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(authTokenUtils.getExpirationTime() / 1000)
                .userId(user.getId())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .maskedPhone(maskedPhone)
                .role(user.getAppUserType() != null ? user.getAppUserType().name() : "USER")
                .build();

    }

    @Override
    public OtpResponseDto requestForgotPassword(ForgotPasswordRequestDto requestDto) {
        String input = requestDto.getPhoneOrUsername().trim();
        Optional<AppUser> userOpt = Optional.empty();

        // Check if input looks like a phone number
        String digits = input.replaceAll("[^0-9+]", "");
        if (digits.length() >= 10) {
            try {
                String normalized = SecurityUtils.normalizePhone(digits);
                userOpt = appUserRepo.findByPhone(normalized);
            } catch (Exception ignored) {
            }
        }

        if (userOpt.isEmpty()) {
            userOpt = appUserRepo.findByUsername(input);
        }

        if (userOpt.isEmpty()) {
          throw  new RuntimeException("No account found matching this phone number or username");
        }

        AppUser user = userOpt.get();
        if (!StringUtils.hasText(user.getPhone())) {
            throw new RuntimeException("This account does not have a verified phone number for password reset");
        }

        return dakpionOtpService.requestOtp(user.getId());
    }

    @Override
    public AuthSessionResponseDto resetPassword(ResetPasswordRequestDto requestDto) {
        OtpVerifyResponseDto verifyResult = dakpionOtpService.verifyOtp(new OtpVerifyRequestDto(requestDto.getRequestId(), requestDto.getCode()));
        String phone = dakpionOtpService.getVerifiedPhone(verifyResult.getVerificationToken(), requestDto.getRequestId());
        if (!StringUtils.hasText(phone)) {
            throw  new RuntimeException("Could not resolve phone number for password reset");
        }

        AppUser user = appUserRepo.findByPhone(phone).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Update password with BCrypt hash
        user.setPassword(passwordEncoder.encode(requestDto.getNewPassword().trim()));
        appUserRepo.save(user);

        // Issue new session so user is logged in immediately
        String accessToken  = authTokenUtils.generateAccessToken(user);
        String refreshToken = authTokenUtils.createRefreshToken(user.getId(), refreshTokenRepo);

        return AuthSessionResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(authTokenUtils.getExpirationTime() / 1000)
                .userId(user.getId())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .maskedPhone(SecurityUtils.maskPhone(user.getPhone()))
                .role(user.getAppUserType() != null ? user.getAppUserType().name() : "USER")
                .build();
    }

    @Override
    public AuthSessionResponseDto refreshToken(RefreshTokenRequestDto refreshTokenRequestDto) {
        if (!StringUtils.hasText(refreshTokenRequestDto.getRefreshToken())) {
            throw  new RuntimeException("refreshToken is required");
        }
        String[] result = authTokenUtils.validateAndRotateRefreshToken(refreshTokenRequestDto.getRefreshToken(), refreshTokenRepo);
        if (result == null) {
            throw new RuntimeException("Refresh token is invalid or expired");
        }

        String newRefreshToken = result[0];
        Long userId            = Long.valueOf(result[1]);

        AppUser user = appUserRepo.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String newAccessToken = authTokenUtils.generateAccessToken(user);

        return AuthSessionResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(authTokenUtils.getExpirationTime() / 1000)
                .userId(user.getId())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .maskedPhone(SecurityUtils.maskPhone(user.getPhone()))
                .role(user.getAppUserType() != null ? user.getAppUserType().name() : "USER")
                .build();
    }

    @Override
    @Transactional
    public Boolean logout(Long userId) {
        refreshTokenRepo.deleteAllByUserId(userId); // or mark revoked, per your schema
        return true;
    }



    private AppUser generateAppUserEntity(RegisterRequestModel registerRequestModel) {
        AppUser entity = new AppUser();
        entity.setUsername(registerRequestModel.getUsername().trim());
        entity.setPhone(SecurityUtils.normalizePhone(registerRequestModel.getPhone()));
        entity.setPassword(passwordEncoder.encode(registerRequestModel.getPassword()));
        entity.setDisplayName(registerRequestModel.getDisplayName());
        entity.setActive(true);
        entity.setAppUserType(AppUserType.USER);
        entity.setUserTypeId(USER_TYPE_ID_USER);
        entity.setEntryDate(LocalDateTime.now());
        entity.setEntryUser(0L);
        return entity;
    }

}
