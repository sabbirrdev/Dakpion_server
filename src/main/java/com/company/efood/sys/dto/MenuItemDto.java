package com.company.efood.sys.dto;

import com.company.efood.base.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemDto extends BaseDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer menuType;
    private Long parentId;
    private String parentName;
    private String menuTypeName;
    private Integer serialNo;
    private String menuUrl;
    private String icon;
    private Boolean view;
    private Boolean insert;
    private Boolean update;
    private Boolean delete;
    private Boolean approve;


}
