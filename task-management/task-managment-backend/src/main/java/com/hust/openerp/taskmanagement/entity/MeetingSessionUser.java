package com.hust.openerp.taskmanagement.entity;

import java.io.Serializable;
import java.util.UUID;

import com.hust.openerp.taskmanagement.multitenancy.entity.AbstractBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "task_management_meeting_session_user")
@IdClass(MeetingSessionUser.MeetingSessionUserId.class)
public class MeetingSessionUser extends AbstractBaseEntity {

	@Id
    @Column(name = "user_id", length = 60)
    private String userId;

	@Id
    @Column(name = "session_id")
    private UUID sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", insertable = false, updatable = false)
    private MeetingSession meetingSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class MeetingSessionUserId implements Serializable {
        private String userId;
        private UUID sessionId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MeetingSessionUserId)) return false;
            MeetingSessionUserId that = (MeetingSessionUserId) o;
            return userId.equals(that.userId) && sessionId.equals(that.sessionId);
        }

        @Override
        public int hashCode() {
            return userId.hashCode() + sessionId.hashCode();
        }
    }
}

