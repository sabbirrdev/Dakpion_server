package com.company.dakpion.dakpion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaqItemDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private LocalizedTextDto question;
    private LocalizedTextDto answer;
}
