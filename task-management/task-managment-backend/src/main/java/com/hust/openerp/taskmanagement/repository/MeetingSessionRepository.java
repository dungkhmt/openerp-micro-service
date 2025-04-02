package com.hust.openerp.taskmanagement.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hust.openerp.taskmanagement.entity.MeetingSession;

@Repository
public interface MeetingSessionRepository extends JpaRepository<MeetingSession, UUID> {
    List<MeetingSession> findByPlanIdOrderByStartTimeAsc(UUID planId);

	Optional<MeetingSession> findFirstByPlanIdOrderByStartTimeAsc(UUID id);

	Optional<MeetingSession> findFirstByPlanIdOrderByEndTimeDesc(UUID id);
   
}

