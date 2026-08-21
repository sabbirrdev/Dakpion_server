package com.company.efood.base;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

/**
 * @Author Md. Sabbir Hossain
 * @Email sabbirr883@gmail.com
 * @Since March 1, 2024
 * @version 1.0.0
 */
@Data
@MappedSuperclass
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 9132875688068247271L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ACTIVE", columnDefinition = "boolean default true")
    private Boolean active = true;

    // Creation audit
    @Column(name = "ENTRY_USER", updatable = false, nullable = false)
    private Long entryUser;

    @Column(name = "ENTRY_DATE", updatable = false, nullable = false)
    private LocalDateTime entryDate;

    // Update audit
    @Column(name = "UPDATE_USER", insertable = false)
    private Long updateUser;

    @Column(name = "UPDATE_DATE", insertable = false)
    private LocalDateTime updateDate;

    @Column(name = "ENTRY_APP_USER_CODE", length = 50)
    private String entryAppUserCode;

    @Column(name = "UPDATE_APP_USER_CODE", length = 50)
    private String updateAppUserCode;

    // ✅ Approval-related fields
    @Column(name = "IS_APPROVED", columnDefinition = "boolean default false")
    private Boolean isApproved = false;

    @Column(name = "APPROVED_BY")
    private Long approvedBy;

    @Column(name = "APPROVED_DATE")
    private LocalDateTime approvedDate;
}

