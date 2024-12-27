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
@Table(name = "hr_staff_salary")
public class StaffSalaryEntity extends AuditEntity {
    @EmbeddedId
    private StaffSalaryId id;

    @Column(name = "thru_date")
    private LocalDate thruDate;

    @Column(name = "salary")
    private Integer salary;

}