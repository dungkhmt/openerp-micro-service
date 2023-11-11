package com.hust.openerp.taskmanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "task_management_project_member")
@IdClass(ProjectMember.ProjectMemberId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectMember {

    @Id
    @Column(name = "project_id")
    private UUID projectId;

    @Id
    @Column(name = "user_id")
    private String userId;

    @Id
    @Column(name = "role_id")
    private String roleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false, insertable = false, updatable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, insertable = false, updatable = false)
    private User member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false, insertable = false, updatable = false)
    private ProjectRole projectRole;

    @CreationTimestamp
    @Column(name = "created_stamp")
    private Date createdStamp;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class ProjectMemberId implements Serializable {
        private UUID projectId;
        private String userId;
        private String roleId;

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof ProjectMemberId))
                return false;
            ProjectMemberId that = (ProjectMemberId) o;
            return projectId.equals(that.projectId) && userId.equals(that.userId)
                && roleId.equals(that.roleId);
        }

        @Override
        public int hashCode() {
            return projectId.hashCode() + userId.hashCode() + roleId.hashCode();
        }
    }
}
