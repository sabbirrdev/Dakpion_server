package com.company.efood.sys.entity;

import com.company.efood.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "SYSTEM_ADMIN")
public class SystemAdmin extends BaseEntity {
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private AppUser appUser;
}

