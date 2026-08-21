package com.company.efood.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BaseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Boolean active;
    private LocalDateTime entryDate;

}
