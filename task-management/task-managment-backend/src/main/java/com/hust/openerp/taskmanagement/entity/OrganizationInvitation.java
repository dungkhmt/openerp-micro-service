package com.hust.openerp.taskmanagement.entity;

import com.hust.openerp.taskmanagement.multitenancy.entity.AbstractBaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "task_management_organization_invitation")
public class OrganizationInvitation extends AbstractBaseEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "organization_id")
    private UUID organizationId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "invited_by", nullable = false)
    private String invitedBy;

    @Column(name = "role_id", nullable = false)
    private String roleId;

    @Column(name = "status_id", nullable = false)
    private String statusId;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "expiration_time", nullable = false)
    private Date expirationTime;

    @CreationTimestamp
    @Column(name = "created_stamp", updatable = false)
    private Date createdStamp;

    @UpdateTimestamp
    @Column(name = "last_updated_stamp")
    private Date lastUpdatedStamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by", insertable = false, updatable = false)
    private User inviter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", insertable = false, updatable = false)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", insertable = false, updatable = false)
    private Organization organization;
}
