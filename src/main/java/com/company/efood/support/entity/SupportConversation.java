package com.company.efood.support.entity;

import com.company.efood.base.BaseEntity;
import com.company.efood.sys.entity.AppUser;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "SUPPORT_CONVERSATION")
public class SupportConversation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    private AppUser customer;

    @Column(name = "SUBJECT")
    private String subject;

    @Column(name = "STATUS")
    private String status = "OPEN";

    @Column(name = "LAST_MESSAGE")
    private String lastMessage;
}
