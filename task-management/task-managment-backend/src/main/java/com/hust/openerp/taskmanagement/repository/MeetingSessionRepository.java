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

    @Query("SELECT s FROM MeetingSession s WHERE s.planId IN :planIds AND s.startTime = (" +
        "SELECT MIN(s2.startTime) FROM MeetingSession s2 WHERE s2.planId = s.planId)")
    List<MeetingSession> findEarliestByPlanIds(@Param("planIds") List<UUID> planIds);

    @Query("SELECT s FROM MeetingSession s WHERE s.planId IN :planIds AND s.endTime = (" +
        "SELECT MAX(s2.endTime) FROM MeetingSession s2 WHERE s2.planId = s.planId)")
    List<MeetingSession> findLatestByPlanIds(@Param("planIds") List<UUID> planIds);
}

