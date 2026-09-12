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
public class ThemePaletteDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String paperBg;
    private String paperTexture;
    private String ink;
    private String accent;
    private String envelope;
    private String seal;
}
