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
public class ThemeDefinitionDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private LocalizedTextDto name;
    private LocalizedTextDto description;
    private String tier;
    private Double price;
    private ThemePaletteDto palette;
    private String previewImage;
    private LocalizedTextDto occasion;
}
