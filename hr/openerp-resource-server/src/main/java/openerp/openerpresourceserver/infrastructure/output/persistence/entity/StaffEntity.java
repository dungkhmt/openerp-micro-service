package openerp.openerpresourceserver.infrastructure.output.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.constant.StaffStatus;

@Getter
@Setter
@Entity
@Table(name = "hr_staff")
public class StaffEntity extends AuditEntity{
    @Id
    @Column(name = "staff_code", nullable = false, length = 100)
    private String staffCode;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_login_id", referencedColumnName = "user_login_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "fullname", length = 200)
    private String fullname;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 100)
    private StaffStatus status;

}