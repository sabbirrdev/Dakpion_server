package com.company.efood.sys.entity;

import com.company.efood.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * @version 1.0.0
 * @Author Md. Sabbir Hossain
 * @Email sabbirr883@gmail.com
 * @Since March 1, 2024
 */
@Entity
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "SYA_USER_ROLE_ASSIGN_DETAILS")
public class UserRoleAssignDetails extends BaseEntity {

    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToOne
    @JoinColumn(name = "MASTER_ID")
    private UserRoleAssignMaster master;

    @OneToOne
    @JoinColumn(name = "USER_ROLE_ID")
    private UserRoleMaster userRole;
}
