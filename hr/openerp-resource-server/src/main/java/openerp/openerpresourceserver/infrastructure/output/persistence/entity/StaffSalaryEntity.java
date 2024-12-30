package openerp.openerpresourceserver.infrastructure.output.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.constant.SalaryType;

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

    @Column(name = "salary_type")
    @Enumerated(EnumType.STRING)
    private SalaryType salaryType;
}