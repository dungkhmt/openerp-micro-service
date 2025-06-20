package com.hust.openerp.taskmanagement.repository;

import com.hust.openerp.taskmanagement.entity.MeetingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MeetingSessionRepository extends JpaRepository<MeetingSession, UUID> {
    List<MeetingSession> findByPlanIdOrderByStartTimeAsc(UUID planId);

    @Query(value = """
        SELECT * FROM task_management_meeting_session s
        WHERE s.plan_id IN (:planIds)
          AND s.start_time = (
              SELECT MIN(s2.start_time)
              FROM task_management_meeting_session s2
              WHERE s2.plan_id = s.plan_id
          )
    """, nativeQuery = true)
    List<MeetingSession> findEarliestByPlanIds(@Param("planIds") List<UUID> planIds);

    @Query(value = """
        SELECT * FROM task_management_meeting_session s
        WHERE s.plan_id IN (:planIds)
          AND s.end_time = (
              SELECT MAX(s2.end_time)
              FROM task_management_meeting_session s2
              WHERE s2.plan_id = s.plan_id
          )
    """, nativeQuery = true)
    List<MeetingSession> findLatestByPlanIds(@Param("planIds") List<UUID> planIds);
}

