package openerp.openerpresourceserver.infrastructure.output.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "hr_staff_job_position")
public class StaffJobPositionEntity extends AuditEntity{
    @EmbeddedId
    private StaffJobPositionId id;

    @Column(name = "thru_date")
    private LocalDate thruDate;

/*
 TODO [Reverse Engineering] create field to map the 'user_id' column
 Available actions: Uncomment as is | Remove column mapping
    @MapsId
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;
*/
/*
 TODO [Reverse Engineering] create field to map the 'position_code' column
 Available actions: Uncomment as is | Remove column mapping
    @MapsId
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "position_code", nullable = false)
    private HrJobPosition positionCode;
*/
}