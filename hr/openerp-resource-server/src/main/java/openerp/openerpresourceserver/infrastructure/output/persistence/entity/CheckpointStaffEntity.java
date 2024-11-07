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
    private CheckpointStaffIdEntity id;

    @Column(name = "checked_by_user_id", length = 60)
    private String checkedByUserId;

    @Column(name = "point")
    private BigDecimal point;

/*
 TODO [Reverse Engineering] create field to map the 'user_id' column
 Available actions: Uncomment as is | Remove column mapping
    @MapsId
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;
*/
/*
 TODO [Reverse Engineering] create field to map the 'checkpoint_code' column
 Available actions: Uncomment as is | Remove column mapping
    @MapsId
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "checkpoint_code", nullable = false)
    private HrCheckpointConfigure checkpointCode;
*/
/*
 TODO [Reverse Engineering] create field to map the 'checkpoint_period_id' column
 Available actions: Uncomment as is | Remove column mapping
    @MapsId
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "checkpoint_period_id", nullable = false)
    private HrCheckpointPeriod checkpointPeriodId;
*/
}