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
@Table(name = "hr_department")
public class Department extends AuditEntity{
    @Id
    @Column(name = "department_code", nullable = false, length = 100)
    private String departmentCode;

    @Column(name = "department_name", length = 200)
    private String departmentName;

    @Column(name = "status", length = 100)
    private String status;

}