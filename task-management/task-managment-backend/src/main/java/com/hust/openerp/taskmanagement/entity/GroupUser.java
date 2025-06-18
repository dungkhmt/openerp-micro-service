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
@Table(name = "task_management_group_user")
@IdClass(GroupUser.GroupUserId.class)
public class GroupUser extends AbstractBaseEntity {
    @Id
    @Column(name = "group_id")
    private UUID groupId;

    @Id
    @Column(name = "user_id", length = 60)
    private String userId;

    @Id
    @Column(name = "from_date")
    private Date fromDate;

    @Column(name = "thrs_date")
    private Date thrsDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", insertable = false, updatable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class GroupUserId implements java.io.Serializable {
        private UUID groupId;
        private String userId;
        private Date fromDate;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof GroupUserId that)) return false;
            return groupId.equals(that.groupId) &&
                userId.equals(that.userId) &&
                fromDate.equals(that.fromDate);
        }

        @Override
        public int hashCode() {
            return groupId.hashCode() + userId.hashCode() + fromDate.hashCode();
        }
    }
}
