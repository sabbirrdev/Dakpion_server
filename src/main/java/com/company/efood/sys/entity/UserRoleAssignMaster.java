package com.company.efood.sys.entity;

import com.company.efood.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(name = "SYA_USER_ROLE_ASSIGN_MASTER")
public class UserRoleAssignMaster extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "APP_USER_ID", nullable = false, unique = true)
    private AppUser appUser;
}
