package com.hust.openerp.taskmanagement.entity;

import com.hust.openerp.taskmanagement.multitenancy.entity.AbstractBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "task_management_organization_user")
@IdClass(OrganizationUser.OrganizationUserId.class)
public class OrganizationUser extends AbstractBaseEntity {
    @Id
    @Column(name = "organization_id")
    private UUID organizationId;

    @Id
    @Column(name = "user_id", length = 60)
    private String userId;

    @Id
    @Column(name = "from_date")
    private Date fromDate;

    @Column(name = "thrs_date")
    private Date thrsDate;

    @Column(name = "role_id")
    private String roleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", insertable = false, updatable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class OrganizationUserId implements java.io.Serializable {
        private UUID organizationId;
        private String userId;
        private Date fromDate;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof OrganizationUserId that)) return false;
            return organizationId.equals(that.organizationId) &&
                userId.equals(that.userId) &&
                fromDate.equals(that.fromDate);
        }

        @Override
        public int hashCode() {
            return organizationId.hashCode() + userId.hashCode() + fromDate.hashCode();
        }
    }
}
