package com.hust.openerp.taskmanagement.entity;

import com.hust.openerp.taskmanagement.multitenancy.entity.AbstractBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "task_management_meeting_plan_user")
@IdClass(MeetingPlanUser.MeetingPlanUserId.class)
public class MeetingPlanUser extends AbstractBaseEntity {

    @Id
    @Column(name = "plan_id")
    private UUID planId;

    @Id
    @Column(name = "user_id", length = 60)
    private String userId;

    @Column(name = "session_id")
    private UUID sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", insertable = false, updatable = false)
    private MeetingPlan meetingPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", insertable = false, updatable = false)
    private MeetingSession meetingSession;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class MeetingPlanUserId implements Serializable {
        private UUID planId;
        private String userId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MeetingPlanUserId)) return false;
            MeetingPlanUserId that = (MeetingPlanUserId) o;
            return planId.equals(that.planId) && userId.equals(that.userId);
        }

        @Override
        public int hashCode() {
            return planId.hashCode() + userId.hashCode();
        }
    }
}

