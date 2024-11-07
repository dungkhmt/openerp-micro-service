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
@Table(name = "hr_staff")
public class StaffEntity extends AuditEntity{
    @Id
    @Column(name = "staff_code", nullable = false, length = 100)
    private String staffCode;

    @Column(name = "user_login_id", length = 60)
    private String userLoginId;

    @Column(name = "fullname", length = 200)
    private String fullname;

    @Column(name = "status", length = 100)
    private String status;

}