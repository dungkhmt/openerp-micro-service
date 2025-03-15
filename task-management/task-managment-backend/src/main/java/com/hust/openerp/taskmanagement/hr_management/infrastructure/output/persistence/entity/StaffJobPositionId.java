package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class StaffJobPositionId implements java.io.Serializable {
    private static final long serialVersionUID = -3481786537321084318L;
    @Column(name = "user_id", nullable = false, length = 60)
    private String userId;

    @Column(name = "position_code", nullable = false, length = 100)
    private String positionCode;

    @Column(name = "from_date", nullable = false)
    private LocalDateTime fromDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        StaffJobPositionId entity = (StaffJobPositionId) o;
        return Objects.equals(this.fromDate, entity.fromDate) &&
                Objects.equals(this.positionCode, entity.positionCode) &&
                Objects.equals(this.userId, entity.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromDate, positionCode, userId);
    }

}