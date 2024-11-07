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
@Table(name = "hr_job_position")
public class JobPosition extends AuditEntity{
    @Id
    @Column(name = "position_code", nullable = false, length = 100)
    private String positionCode;

    @Column(name = "position_name", length = 500)
    private String positionName;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

}