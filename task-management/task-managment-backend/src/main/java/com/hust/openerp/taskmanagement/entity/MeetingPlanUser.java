package com.hust.openerp.taskmanagement.entity;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "task_management_meeting_plan_user")
@IdClass(MeetingPlanUser.MeetingPlanUserId.class)
public class MeetingPlanUser {

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

    @SuppressWarnings("serial")
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

