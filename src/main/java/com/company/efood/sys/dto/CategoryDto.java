package com.company.efood.sys.dto;

import com.company.efood.base.BaseDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto extends BaseDto implements Serializable {
    public Long parentId;
    public String parentName;
    public String categoryName;
    public String categoryNameBangla;
    public String description;

    /** Compatibility alias for frontend that uses `name` */
    @JsonProperty("name")
    public String getName() {
        return categoryName;
    }

    @JsonProperty("name")
    public void setName(String name) {
        if (this.categoryName == null || this.categoryName.isBlank()) {
            this.categoryName = name;
        }
    }
}
