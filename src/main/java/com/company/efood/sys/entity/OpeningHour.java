package com.company.efood.sys.entity;

import com.company.efood.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "OPENING_HOUR")
@Data
@EqualsAndHashCode(callSuper = true)
public class OpeningHour extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BRANCH_ID", nullable = false)
    private Branch branch;

    @Enumerated(EnumType.STRING)
    @Column(name = "DAY", nullable = false, length = 10)
    private DayOfWeek day; // java.time.DayOfWeek (MONDAY to SUNDAY)

    @Column(name = "OPEN_TIME", nullable = false)
    private LocalTime openTime;

    @Column(name = "CLOSE_TIME", nullable = false)
    private LocalTime closeTime;
}


