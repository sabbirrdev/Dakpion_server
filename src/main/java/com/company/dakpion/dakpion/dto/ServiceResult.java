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
public class ServiceResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Builder.Default
    private boolean success = true;
    private String message;
    private T data;

    public static <T> ServiceResult<T> of(T data) {
        return ServiceResult.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    public static <T> ServiceResult<T> ofSuccess(String message) {
        return ServiceResult.<T>builder()
                .success(true)
                .message(message)
                .build();
    }

    public static <T> ServiceResult<T> of(T data, String message) {
        return ServiceResult.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }
}
