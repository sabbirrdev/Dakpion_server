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
public class FontDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String fontFamily;
    private String category;
    private String cssUrl;
    private String previewSample;
    private Boolean active;
    private Integer displayOrder;
}
