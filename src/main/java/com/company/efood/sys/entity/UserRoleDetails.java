package com.company.efood.sys.entity;

import com.company.efood.base.BaseEntity;
import jakarta.persistence.*;
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
@Table(name = "SYA_USER_ROLE_DETAILS")
public class UserRoleDetails extends BaseEntity {

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    @JoinColumn(name = "MASTER_ID")
    private UserRoleMaster master;

    @OneToOne
    @JoinColumn(name = "MENU_ITEM_ID")
    private MenuItem menuItem;

    @Column(name = "IS_VIEW", columnDefinition = "boolean default false")
    private Boolean view;

    @Column(name = "IS_INSERT", columnDefinition = "boolean default false")
    private Boolean insert;

    @Column(name = "IS_UPDATE", columnDefinition = "boolean default false")
    private Boolean update;

    @Column(name = "IS_DELETE", columnDefinition = "boolean default false")
    private Boolean delete;
    @Column(name = "IS_APPROVE", columnDefinition = "boolean default false")
    private Boolean approve = false;
}
