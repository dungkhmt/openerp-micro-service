package openerp.openerpresourceserver.infrastructure.output.persistence.entity;

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
public class CheckpointStaffId implements java.io.Serializable {
    private static final long serialVersionUID = 8808781507810867549L;
    @Column(name = "user_id", nullable = false, length = 60)
    private String userId;

    @Column(name = "checkpoint_code", nullable = false, length = 100)
    private String checkpointCode;

    @Column(name = "checkpoint_period_id", nullable = false)
    private UUID checkpointPeriodId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CheckpointStaffId entity = (CheckpointStaffId) o;
        return Objects.equals(this.checkpointPeriodId, entity.checkpointPeriodId) &&
                Objects.equals(this.checkpointCode, entity.checkpointCode) &&
                Objects.equals(this.userId, entity.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(checkpointPeriodId, checkpointCode, userId);
    }

}