package com.company.dakpion.base;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/*
 * @Author       Md.Sabbir Hossain
 * @Email        sabbirr883@gmail.com
 * @Since        March 1, 2024
 * @Version      1.0.0
 */
@Component
public class BaseUtils implements BaseConstants {

    public BaseResponse generateSuccessResponse(Object obj, String... message) {
        return BaseResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .status(true)
                .data(obj)
                .message(message.length > 0 ? message[0] : null)
                .messageBn(message.length > 1 ? message[1] : null)
                .build();
    }
    public BaseResponse generateErrorResponse(Exception e) {
        BaseResponse.BaseResponseBuilder response = BaseResponse.builder();
        response.statusCode(HttpStatus.BAD_REQUEST.value());
        response.status(false);
        String msgType = getMessageType(e.getMessage());
        if (msgType.equals("uk") || msgType.equals("re")) {
            response.statusCode(HttpStatus.FOUND.value());
            response.message(DATA_ALRADY_EXISTS_MESSAGE);
            response.message(DATA_ALRADY_EXISTS_MESSAGE_BN);
        }
        else if (msgType.equals("fk")) {
            response.statusCode(HttpStatus.PARTIAL_CONTENT.value());
            response.message(CHILD_RECORD_FOUND);
            response.message(CHILD_RECORD_FOUND_BN);
        } else {
            response.message(e.getMessage());
        }
        return response.build();
    }
    private String getMessageType(String message) {
        if (message != null && message.length() > 55) {
            return message.substring(52, 54);
        }
        return "";
    }

    public void setEntryUserInfo(Object obj) {
        BaseEntity entity = (BaseEntity) obj;
        /* set entry date */
        entity.setEntryDate(LocalDateTime.now());
    }

    public void setUpdateUserInfo(Object obj, Object dbEntity) {
        BaseEntity entity = (BaseEntity) obj;
        BaseEntity data = (BaseEntity) dbEntity;

        /* set previous entry User Info */
        entity.setEntryUser(data.getEntryUser());
        entity.setEntryDate(data.getEntryDate());
        /* set update date */
        entity.setUpdateDate(LocalDateTime.now());

    }


    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }


    public PageRequest getPageRequest(int page, int size) {
//      PageRequest.of(page, size, Sort.by("price").descending().and(Sort.by("name")));
//      PageRequest pageRequest = PageRequest.of(page, size, Sort.by("entryDate").descending());
        return PageRequest.of(page, size, Sort.by("id").descending());
    }

    // Function for Generate Random unique number
    public String generateRequestId(String prefix, String suffix) {
        // Get the current date
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        // Generate a random UUID
        String uuid = UUID.randomUUID().toString();
        // Combine prefix, date, UUID, and suffix to create the request ID

        return (prefix != null ? prefix : "") + date + "-" + uuid + (suffix != null ? suffix : "");
    }


}
