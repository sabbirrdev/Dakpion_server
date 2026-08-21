package com.company.efood.sys.entity;

import com.company.efood.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @version 1.0.0
 * @Author Md. Sabbir Hossain
 * @Email sabbirr883@gmail.com
 * @Since March 1, 2024
 */
@Entity
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "SYA_MENU_ITEM")
public class MenuItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "NAME", length = 100)
    private String name;

    @Column(name = "BANGLA_NAME", length = 100)
    private String banglaName;

    @Column(name = "MENU_TYPE")
    private Integer menuType;

    @Column(name = "MENU_TYPE_NAME", length = 100)
    private String menuTypeName;

    @Column(name = "SERIAL_NO")
    private Integer serialNo;

    @Column(name = "MENU_URL")
    private String menuUrl;

    @Column(name = "ICON", length = 100)
    private String menuIcon;

    @Column(name = "IS_VIEW", columnDefinition = "boolean default false")
    private Boolean view = false;

    @Column(name = "IS_INSERT", columnDefinition = "boolean default false")
    private Boolean insert = false;

    @Column(name = "IS_UPDATE", columnDefinition = "boolean default false")
    private Boolean update = false;

    @Column(name = "IS_DELETE", columnDefinition = "boolean default false")
    private Boolean delete = false;

    @Column(name = "IS_APPROVE", columnDefinition = "boolean default false")
    private Boolean approve = false;

    @ManyToOne
    @JoinColumn(name = "PARENT_ID")
    private MenuItem parent;


}
