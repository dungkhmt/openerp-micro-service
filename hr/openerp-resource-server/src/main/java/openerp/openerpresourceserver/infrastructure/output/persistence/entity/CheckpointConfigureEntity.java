package openerp.openerpresourceserver.infrastructure.output.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "hr_checkpoint_configure")
public class CheckpointConfigureEntity extends AuditEntity{
    @Id
    @Column(name = "checkpoint_code", nullable = false, length = 100)
    private String checkpointCode;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @Column(name = "status", length = 100)
    private String status;

}