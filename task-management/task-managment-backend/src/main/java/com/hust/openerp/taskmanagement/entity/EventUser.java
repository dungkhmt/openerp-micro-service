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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "task_management_event_user")
@IdClass(EventUser.EventUserId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventUser {

    @Id
    @Column(name = "event_id")
    private UUID eventId;

    @Id
    @Column(name = "user_id")
    private String userId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false, insertable = false, updatable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, insertable = false, updatable = false)
    private User user;


    @SuppressWarnings("serial")
	@AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class EventUserId implements Serializable {
        private UUID eventId;
        private String userId;

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof EventUserId))
                return false;
            EventUserId that = (EventUserId) o;
            return eventId.equals(that.eventId) && userId.equals(that.userId);
        }

        @Override
        public int hashCode() {
            return eventId.hashCode() + userId.hashCode();
        }
    }
}
