package openerp.openerpresourceserver.infrastructure.output.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "hr_staff_job_position")
public class StaffJobPositionEntity extends AuditEntity{
    @EmbeddedId
    private StaffJobPositionId id;

    @Column(name = "thru_date")
    private LocalDateTime thruDate;

}