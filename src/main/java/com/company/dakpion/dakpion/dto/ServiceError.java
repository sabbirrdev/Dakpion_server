package com.company.dakpion.dakpion.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceError implements Serializable {
    private static final long serialVersionUID = 1L;

    @Builder.Default
    private boolean success = false;
    private String code;
    private String message;

    public static ServiceError of(String code, String message) {
        return ServiceError.builder()
                .success(false)
                .code(code)
                .message(message)
                .build();
    }
}
