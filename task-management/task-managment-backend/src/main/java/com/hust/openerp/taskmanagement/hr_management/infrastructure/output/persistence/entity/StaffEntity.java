package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity;

import com.hust.openerp.taskmanagement.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.hust.openerp.taskmanagement.hr_management.constant.StaffStatus;

@Getter
@Setter
@Entity
@Table(name = "hr_staff")
public class StaffEntity extends AuditEntity{
    @Id
    @Column(name = "staff_code", nullable = false, length = 100)
    private String staffCode;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_login_id", referencedColumnName = "user_login_id", updatable = false)
    private User user;

    @Column(name = "fullname", length = 200)
    private String fullname;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 100)
    private StaffStatus status;

}