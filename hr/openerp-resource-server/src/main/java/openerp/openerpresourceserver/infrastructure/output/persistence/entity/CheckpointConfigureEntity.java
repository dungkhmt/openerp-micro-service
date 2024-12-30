package openerp.openerpresourceserver.infrastructure.output.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.constant.CheckpointConfigureStatus;

@Getter
@Setter
@Entity
@Table(name = "hr_checkpoint_configure")
public class CheckpointConfigureEntity extends AuditEntity{
    @Id
    @Column(name = "checkpoint_code", nullable = false, length = 100)
    private String checkpointCode;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 100)
    private CheckpointConfigureStatus status;
}