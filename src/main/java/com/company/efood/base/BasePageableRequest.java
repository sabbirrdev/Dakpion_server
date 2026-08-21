package com.company.efood.base;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class BasePageableRequest {
    private Integer page;
    private Integer size;
    private String searchValue;
    
    private Long intParam1;
    private Long intParam2;
    private Long intParam3;
    private Long intParam4;
    private Long intParam5;
    private Long intParam6;
    private String stringParam1;
    private String stringParam2;
    private String stringParam3;
    private LocalDateTime dateParam1;
    private LocalDateTime dateParam2;

    private List<Integer> listIntParam1;
}
