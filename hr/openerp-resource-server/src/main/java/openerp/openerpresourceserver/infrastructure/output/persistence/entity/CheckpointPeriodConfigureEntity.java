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
@Table(name = "hr_checkpoint_period_configure")
public class CheckpointPeriodConfigureEntity extends AuditEntity{
    @EmbeddedId
    private CheckpointPeriodConfigureId id;

    @Column(name = "coefficient")
    private BigDecimal coefficient;

    @Column(name = "status", length = 100)
    private String status;


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