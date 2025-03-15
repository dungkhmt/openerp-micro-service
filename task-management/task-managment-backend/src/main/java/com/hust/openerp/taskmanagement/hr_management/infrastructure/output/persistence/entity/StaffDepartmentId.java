package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class StaffDepartmentId implements java.io.Serializable {
    private static final long serialVersionUID = -6592692242483892864L;
    @Column(name = "user_id", nullable = false, length = 60)
    private String userId;

    @Column(name = "department_code", nullable = false, length = 100)
    private String departmentCode;

    @Column(name = "from_date", nullable = false)
    private LocalDateTime fromDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        StaffDepartmentId entity = (StaffDepartmentId) o;
        return Objects.equals(this.fromDate, entity.fromDate) &&
                Objects.equals(this.departmentCode, entity.departmentCode) &&
                Objects.equals(this.userId, entity.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromDate, departmentCode, userId);
    }

}