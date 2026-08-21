package com.company.efood.sys.entity;

import com.company.efood.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
@Table(name = "SYA_USER_ROLE_MASTER")
public class UserRoleMaster extends BaseEntity {

    @Column(name = "NAME", length = 100, nullable = false, unique = true)
    private String name;

    @Column(name = "BANGLA_NAME", length = 100, nullable = false, unique = true)
    private String banglaName;

}
