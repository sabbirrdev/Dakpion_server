package com.company.efood.sys.controller;

import com.company.efood.base.BaseResponse;
import com.company.efood.sys.dto.PasswordPolicyDto;
import com.company.efood.sys.dto.RefreshTokenDto;
import com.company.efood.sys.entity.AppUser;
import com.company.efood.sys.entity.PasswordPolicy;
import com.company.efood.sys.model.AuthResponseModel;
import com.company.efood.sys.model.CustomUserDetails;
import com.company.efood.sys.model.LoginRequestModel;
import com.company.efood.sys.model.RegisterRequestModel;
import com.company.efood.sys.repository.AppUserRepo;
import com.company.efood.sys.services.EmailOtpService;
import com.company.efood.sys.services.PasswordPolicyService;
import com.company.efood.sys.services.RegisterService;
import com.company.efood.sys.utils.AppUserType;
import com.company.efood.sys.utils.AuthTokenUtils;
import com.company.efood.sys.utils.Gender;
import com.company.efood.user.repository.CustomerRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

import static com.company.efood.base.BaseConstants.AUTHENTICATION_ENDPOINT;
import static com.company.efood.base.BaseConstants.USER_TYPE_ID_CUSTOMER;

@RestController
@RequestMapping(AUTHENTICATION_ENDPOINT)
public class AuthController {

    @Value("${app-jwt-expiration-milliseconds}")
    private long expiration;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthTokenUtils authTokenUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RegisterService registerService;

    @Autowired
    private PasswordPolicyService passwordPolicyService;

    @Autowired
    private EmailOtpService emailOtpService;

    @Autowired
    private AppUserRepo appUserRepo;

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private com.company.efood.user.services.ReferralService referralService;

    @PostMapping("signin")
    public ResponseEntity<BaseResponse> signIn(@Valid @RequestBody LoginRequestModel requestModel) {
        System.out.println("🔐 [AUTH CONTROLLER SIGNIN] Attempting login for username: " + requestModel.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestModel.getUsername(),
                            requestModel.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            String token = authTokenUtils.generateJWTToken(authentication);
            RefreshTokenDto refreshTokenDto = authTokenUtils.generateRefreshToken(requestModel.getUsername());

            System.out.println("✅ [AUTH CONTROLLER SIGNIN SUCCESS] User: " + requestModel.getUsername() + " | UserTypeId: " + userDetails.getUserTypeId() + " | AppUserType: " + userDetails.getAppUserType());

            BaseResponse response = BaseResponse.builder()
                    .status(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Login Successful")
                    .messageBn("")
                    .data(new AuthResponseModel(token, refreshTokenDto.getRefreshToken(), userDetails.getUserTypeId(),
                            userDetails.getAppUserType(), System.currentTimeMillis(), expiration))
                    .build();

            return ResponseEntity.ok(response);

        } catch (InternalAuthenticationServiceException e) {
            System.out.println("❌ [AUTH CONTROLLER SIGNIN ERROR] Internal auth error: " + e.getMessage());
            BaseResponse response = BaseResponse.builder()
                    .status(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

        } catch (Exception e) {
            System.out.println("❌ [AUTH CONTROLLER SIGNIN ERROR] Bad credentials or login failed: " + e.getMessage());
            BaseResponse response = BaseResponse.builder()
                    .status(false)
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("Wrong username or password")
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        System.out.println("🔄 [AUTH CONTROLLER REFRESH] Received refresh request with token: " + (refreshToken != null ? "PRESENT" : "NULL"));

        if (refreshToken == null || !authTokenUtils.verifyRefreshToken(refreshToken)) {
            System.out.println("❌ [AUTH CONTROLLER REFRESH ERROR] Invalid or expired refresh token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

        RefreshTokenDto refreshTokenDto = authTokenUtils.getRefreshToken(refreshToken);
        Long userId = refreshTokenDto.getAppUserId();

        if (userId == null) {
            System.out.println("❌ [AUTH CONTROLLER REFRESH ERROR] AppUserId is NULL in refresh token record!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token has no associated user. Please log in again.");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(
                authTokenUtils.getUsernameById(userId));

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String newAccessToken = authTokenUtils.generateJWTToken(authentication);
        System.out.println("🎉 [AUTH CONTROLLER REFRESH SUCCESS] Successfully issued new accessToken for userId: " + userId);

        return ResponseEntity.ok(Map.of(
                "accessToken", newAccessToken,
                "refreshToken", refreshToken
        ));
    }

    @PostMapping("signup")
    public ResponseEntity<BaseResponse> registerUser(@Valid @RequestBody RegisterRequestModel requestModel,
            HttpServletRequest request) {

        final String password = requestModel.getPassword();
        PasswordPolicyDto passwordPolicy;

        try {
            passwordPolicy = passwordPolicyService.getPublicPasswordPolicyById(1L);
        } catch (Exception e) {
            BaseResponse response = BaseResponse.builder()
                    .status(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Error retrieving password policy")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        if (requestModel.getUsername().isEmpty() || password.isEmpty() || requestModel.getPhone() == null || requestModel.getPhone().trim().isEmpty()) {
            BaseResponse response = BaseResponse.builder()
                    .status(false)
                    .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                    .message("Username, Password, and Phone Number cannot be empty")
                    .build();
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
        }

        if (registerService.getUserByUsername(requestModel.getUsername()).isPresent()) {
            BaseResponse response = BaseResponse.builder()
                    .status(false)
                    .statusCode(HttpStatus.CONFLICT.value())
                    .message("User already exists")
                    .build();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        if (password.length() < passwordPolicy.getMinLength()) {
            BaseResponse response = BaseResponse.builder()
                    .status(false)
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("Password must be at least " + passwordPolicy.getMinLength() + " characters long")
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            AppUser newUser = generateAppUserEntity(requestModel, passwordPolicy);
            AppUser savedUser = registerService.addUser(newUser);
            populateCustomerProfile(savedUser, requestModel);

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestModel.getUsername(), password));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = authTokenUtils.generateJWTToken(authentication);
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            RefreshTokenDto refreshTokenDto = authTokenUtils.generateRefreshToken(requestModel.getUsername());

            BaseResponse response = BaseResponse.builder()
                    .status(true)
                    .statusCode(HttpStatus.CREATED.value())
                    .message("Registration successful")
                    .messageBn("")
                    .data(new AuthResponseModel(token, refreshTokenDto.getRefreshToken(), userDetails.getUserTypeId(),
                            userDetails.getAppUserType(), System.currentTimeMillis(), expiration))
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            BaseResponse response = BaseResponse.builder()
                    .status(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Registration failed: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/email-otp/request")
    public ResponseEntity<BaseResponse> requestEmailOtp(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        if (username == null || username.isBlank()) {
            BaseResponse response = BaseResponse.builder()
                    .status(false)
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("Username is required")
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        AppUser user = appUserRepo.findByUsername(username).orElse(null);
        if (user == null) {
            BaseResponse response = BaseResponse.builder()
                    .status(false)
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .message("User not found")
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        String otp = String.format("%06d", (int) (Math.random() * 1000000));
        user.setOtp(otp);
        user.setOtpExpiresAt(LocalDateTime.now().plusMinutes(5));
        user.setEmailVerified(false);
        user.setUpdateDate(LocalDateTime.now());
        appUserRepo.save(user);

        boolean mailSent = emailOtpService.sendOtpEmail(username, otp);
        BaseResponse response = BaseResponse.builder()
                .status(true)
                .statusCode(HttpStatus.OK.value())
                .message(mailSent ? "Email OTP sent successfully" : "Email OTP generated in development fallback mode")
                .data(mailSent
                        ? Map.of("expiresAt", user.getOtpExpiresAt().toString())
                        : Map.of("otp", otp, "expiresAt", user.getOtpExpiresAt().toString()))
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/email-otp/verify")
    public ResponseEntity<BaseResponse> verifyEmailOtp(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String otp = body.get("otp");
        if (username == null || username.isBlank() || otp == null || otp.isBlank()) {
            BaseResponse response = BaseResponse.builder()
                    .status(false)
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("Username and OTP are required")
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        AppUser user = appUserRepo.findByUsername(username).orElse(null);
        if (user == null) {
            BaseResponse response = BaseResponse.builder()
                    .status(false)
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .message("User not found")
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        if (user.getOtpExpiresAt() == null || user.getOtpExpiresAt().isBefore(LocalDateTime.now())) {
            BaseResponse response = BaseResponse.builder()
                    .status(false)
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("OTP expired")
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (!otp.equals(user.getOtp())) {
            BaseResponse response = BaseResponse.builder()
                    .status(false)
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("OTP did not match")
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        user.setEmailVerified(true);
        user.setOtp(null);
        user.setOtpExpiresAt(null);
        user.setUpdateDate(LocalDateTime.now());
        appUserRepo.save(user);

        BaseResponse response = BaseResponse.builder()
                .status(true)
                .statusCode(HttpStatus.OK.value())
                .message("Email verified successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    private AppUser generateAppUserEntity(RegisterRequestModel requestModel, PasswordPolicyDto passwordPolicy) {
        AppUser appUser = new AppUser();
        appUser.setDisplayName(requestModel.getDisplayName());
        appUser.setUsername(requestModel.getUsername());
        appUser.setPassword(passwordEncoder.encode(requestModel.getPassword()));
        appUser.setPasswordPolicy(generatePasswordPolicyEntity(passwordPolicy));
        appUser.setEntryUser(0L);
        appUser.setEntryDate(LocalDateTime.now());
        appUser.setAppUserType(AppUserType.CUSTOMER);
        appUser.setUserTypeId(USER_TYPE_ID_CUSTOMER);
        appUser.setEmailVerified(false);
        return appUser;
    }

    private PasswordPolicy generatePasswordPolicyEntity(PasswordPolicyDto passwordPolicy) {
        PasswordPolicy entity = new PasswordPolicy();
        BeanUtils.copyProperties(passwordPolicy, entity);
        return entity;
    }

    private void populateCustomerProfile(AppUser savedUser, RegisterRequestModel requestModel) {
        if (savedUser == null || !AppUserType.CUSTOMER.equals(savedUser.getAppUserType())) {
            return;
        }

        customerRepo.findByAppUserId(savedUser.getId()).ifPresent(customer -> {
            if (requestModel.getDisplayName() != null && !requestModel.getDisplayName().isBlank()) {
                customer.setFullName(requestModel.getDisplayName().trim());
            }
            if (requestModel.getPhone() != null && !requestModel.getPhone().isBlank()) {
                customer.setPhone(requestModel.getPhone().trim());
            }
            if (requestModel.getUsername() != null && !requestModel.getUsername().isBlank()) {
                customer.setEmail(requestModel.getUsername().trim());
            }
            Gender gender = resolveGender(requestModel.getGender());
            if (gender != null) {
                customer.setGender(gender);
            }
            customerRepo.save(customer);

            // Initialize unique referral code and link referrer if referralCode supplied
            referralService.initReferral(customer, requestModel.getReferralCode());
        });
    }

    private Gender resolveGender(String genderValue) {
        if (genderValue == null || genderValue.isBlank()) {
            return null;
        }

        String normalized = genderValue.trim().toUpperCase();
        return switch (normalized) {
            case "MALE" -> Gender.MALE;
            case "FEMALE" -> Gender.FEMALE;
            case "OTHER" -> Gender.OTHER;
            default -> null;
        };
    }
}