package com.company.dakpion.dakpion.service.impl;

import com.company.dakpion.base.BasePageableRequest;
import com.company.dakpion.base.BaseUtils;
import com.company.dakpion.config.CurrentUserContext;
import com.company.dakpion.dakpion.config.DakpionProperties;
import com.company.dakpion.dakpion.dto.LetterResponseDto;
import com.company.dakpion.dakpion.dto.OtpVerifyRequestDto;
import com.company.dakpion.dakpion.dto.OtpVerifyResponseDto;
import com.company.dakpion.dakpion.entity.DakpionLetterEntity;
import com.company.dakpion.dakpion.repository.DakpionLetterRepo;
import com.company.dakpion.dakpion.service.DakpionOtpService;
import com.company.dakpion.dakpion.service.DakpionProfileService;
import com.company.dakpion.sys.dto.AppUserDto;
import com.company.dakpion.sys.entity.AppUser;
import com.company.dakpion.sys.model.CurrentUserInfo;
import com.company.dakpion.sys.repository.AppUserRepo;
import com.company.dakpion.sys.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import com.company.dakpion.dakpion.mapper.DakpionMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static com.company.dakpion.sys.utils.SecurityUtils.maskPhone;

@Service
@AllArgsConstructor
public class DakpionProfileServiceImpl implements DakpionProfileService {

    private final DakpionMapper dakpionMapper;
    private final AppUserRepo appUserRepo;
    private final BaseUtils baseUtils;
    private final DakpionLetterRepo dakpionLetterRepo;
    private final DakpionOtpService dakpionOtpService;
    private  final DakpionProperties properties;


    @Override
    public AppUserDto getProfile(Long userId) {
        CurrentUserInfo currentUser = CurrentUserContext.get();
        if (currentUser == null || currentUser.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }
        AppUser appUser = appUserRepo.findById(currentUser.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return generateAppUserDto(appUser);
    }

    @Override
    public Page<LetterResponseDto> getSentLetters(BasePageableRequest pageableBodyRequest, Long userId) {
        Long targetUserId = (userId != null) ? userId : (CurrentUserContext.get() != null ? CurrentUserContext.get().getUserId() : null);
        if (targetUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }
        AppUser appUser = appUserRepo.findById(targetUserId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        int page = (pageableBodyRequest != null && pageableBodyRequest.getPage() != null) ? pageableBodyRequest.getPage() : 0;
        int size = (pageableBodyRequest != null && pageableBodyRequest.getSize() != null && pageableBodyRequest.getSize() > 0) ? pageableBodyRequest.getSize() : 20;
        PageRequest pageRequest = baseUtils.getPageRequest(page, size);

        if (appUser.getPhone() == null || appUser.getPhone().isBlank()) {
            return new PageImpl<>(List.of(), pageRequest, 0);
        }

        String phoneHashed = SecurityUtils.hashPhoneWithPepper(appUser.getPhone(), properties.getSecurity().getPhonePepper());
        System.out.println("SENDER PHONE HASHED :_____________");
        System.out.println(phoneHashed);
        Page<DakpionLetterEntity> letterEntityPage = dakpionLetterRepo.findAllBySenderPhoneHashedOrderByCreatedAtDesc(phoneHashed, pageRequest);
        return new PageImpl<>(convertEntityListToDtoList(letterEntityPage.stream()), pageRequest, letterEntityPage.getTotalElements());
    }

    @Override
    public Page<LetterResponseDto> getReceivedLetters(BasePageableRequest pageableBodyRequest, Long userId) {
        Long targetUserId = (userId != null) ? userId : (CurrentUserContext.get() != null ? CurrentUserContext.get().getUserId() : null);
        if (targetUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }
        AppUser appUser = appUserRepo.findById(targetUserId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        int page = (pageableBodyRequest != null && pageableBodyRequest.getPage() != null) ? pageableBodyRequest.getPage() : 0;
        int size = (pageableBodyRequest != null && pageableBodyRequest.getSize() != null && pageableBodyRequest.getSize() > 0) ? pageableBodyRequest.getSize() : 20;
        PageRequest pageRequest = baseUtils.getPageRequest(page, size);

        Page<DakpionLetterEntity> letterEntityPage;
        String phone = appUser.getPhone() != null ? appUser.getPhone().replaceAll("[^0-9+]", "") : null;

        if (phone != null && !phone.isBlank()) {
            // Find letters by phone OR by userId (covers backfilled & new letters)
            letterEntityPage = dakpionLetterRepo.findAllByRecipientPhoneOrRecipientUserIdOrderByCreatedAtDesc(phone, appUser.getId(), pageRequest);
        } else {
            letterEntityPage = dakpionLetterRepo.findAllByRecipientUserIdOrderByCreatedAtDesc(appUser.getId(), pageRequest);
        }
        return new PageImpl<>(convertEntityListToDtoList(letterEntityPage.stream()), pageRequest, letterEntityPage.getTotalElements());
    }

    @Override
    public AppUserDto verifyPhone(OtpVerifyRequestDto otpVerifyRequestDto, Long userId) {
        CurrentUserInfo currentUser = CurrentUserContext.get();
        if (currentUser == null || currentUser.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }

        String requestId = otpVerifyRequestDto.getRequestId();
        String code = otpVerifyRequestDto.getCode();
        if (!StringUtils.hasText(requestId) || !StringUtils.hasText(code)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "requestId and code are required");
        }

        OtpVerifyResponseDto verifyResult = dakpionOtpService.verifyOtp(new OtpVerifyRequestDto(requestId, code));
        if (!verifyResult.isVerified()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired OTP code");
        }

        AppUser appUser = appUserRepo.findById(currentUser.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String phone = dakpionOtpService.getVerifiedPhone(verifyResult.getVerificationToken(), requestId);
        if (StringUtils.hasText(phone)) {
            appUser.setPhone(phone);
        }
        appUser.setPhoneVerified(true);
        appUserRepo.save(appUser);

        // Backfill any letters addressed to this verified phone
        if (StringUtils.hasText(phone)) {
            try {
                dakpionLetterRepo.findAllByRecipientPhoneOrderByCreatedAtDesc(phone).forEach(letter -> {
                    if (letter.getRecipientUserId() == null) {
                        letter.setRecipientUserId(appUser.getId());
                        dakpionLetterRepo.save(letter);
                    }
                });
            } catch (Exception e) {
               // log.warn("Letter backfill failed for phone={}, continuing: {}", maskPhone(phone), e.getMessage());
            }
        }
        return  generateAppUserDto(appUser);
    }

    @Override
    public AppUserDto updateProfile(AppUserDto appUserDto, Long userId) {
        CurrentUserInfo currentUser = CurrentUserContext.get();
        if (currentUser == null || currentUser.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }
        AppUser entity = appUserRepo.save(generateAppUserEntity(appUserDto,currentUser.getUserId()));
        return generateAppUserDto(entity);
    }

    /// /////////////////////////////////
    // HELPPER FUNCTION
    /// ////////////////////////////////


    private AppUser generateAppUserEntity(AppUserDto appUserDto, Long userId) {

        AppUser entity = new AppUser();
        BeanUtils.copyProperties(appUserDto, entity);
        AppUser dbEntity = appUserRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        entity.setId(dbEntity.getId());
        entity.setPassword(dbEntity.getPassword());
        entity.setUpdateUser(userId);
        entity.setPhone(SecurityUtils.normalizePhone(appUserDto.getPhone()));
        baseUtils.setUpdateUserInfo(entity, dbEntity);
        return entity;
    }


    public AppUserDto generateAppUserDto(AppUser entity) {
        AppUserDto dto = new AppUserDto();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setPhone(maskPhone(entity.getPhone()));
        dto.setDisplayName(entity.getDisplayName());
        dto.setAppUserType(entity.getAppUserType());
        dto.setEntryDate(entity.getEntryDate());
        dto.setPhoneVerified(entity.getPhoneVerified());
        dto.setPassword(null);
        return dto;
    }

    public LetterResponseDto generateLetterDto(DakpionLetterEntity entity) {
        // Use the safe MapStruct mapper which handles jsonb shippingAddress gracefully
        LetterResponseDto letterResponseDto = dakpionMapper.toLetterDto(entity);
        letterResponseDto.setSenderPhoneHashed(null);
        return letterResponseDto;
    }

    private List<LetterResponseDto> convertEntityListToDtoList(Stream<DakpionLetterEntity> entityList) {
        return entityList.map(this::generateLetterDto).collect(Collectors.toList());
    }
}
