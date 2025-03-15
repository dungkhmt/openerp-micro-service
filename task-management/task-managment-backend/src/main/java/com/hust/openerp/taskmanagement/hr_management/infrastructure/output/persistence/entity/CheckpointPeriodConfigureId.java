package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Embeddable
public class CheckpointPeriodConfigureId implements java.io.Serializable {
    private static final long serialVersionUID = 7246862366594370700L;
    @Column(name = "checkpoint_code", nullable = false, length = 100)
    private String checkpointCode;

    @Column(name = "checkpoint_period_id", nullable = false)
    private UUID checkpointPeriodId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CheckpointPeriodConfigureId entity = (CheckpointPeriodConfigureId) o;
        return Objects.equals(this.checkpointPeriodId, entity.checkpointPeriodId) &&
                Objects.equals(this.checkpointCode, entity.checkpointCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(checkpointPeriodId, checkpointCode);
    }

}