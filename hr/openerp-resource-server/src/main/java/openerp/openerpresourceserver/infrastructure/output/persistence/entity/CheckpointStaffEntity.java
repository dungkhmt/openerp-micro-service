package openerp.openerpresourceserver.infrastructure.output.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "hr_checkpoint_staff")
public class CheckpointStaffEntity extends AuditEntity{
    @EmbeddedId
    private CheckpointStaffId id;

    @Column(name = "checked_by_user_id", length = 60)
    private String checkedByUserId;

    @Column(name = "point")
    private BigDecimal point;
}