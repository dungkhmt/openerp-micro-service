package com.hust.openerp.taskmanagement.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hust.openerp.taskmanagement.entity.EventUser;
import com.hust.openerp.taskmanagement.entity.User;

import jakarta.transaction.Transactional;

@Repository
public interface EventUserRepository extends JpaRepository<EventUser, EventUser.EventUserId> {
	
	@Query("SELECT eu.user FROM EventUser eu WHERE eu.event.id = :eventId")
	List<User> findUsersByEventId(@Param("eventId") UUID eventId);
	
	@Modifying
    @Transactional
    void deleteByEventId(UUID eventId);
	
	@Modifying
    @Transactional
    @Query(value = """
        DELETE FROM task_management_event_user e
        USING task_management_event ev
        WHERE e.event_id = ev.id
        AND ev.project_id = :projectId
        AND e.user_id = :memberId
    """, nativeQuery = true)
    void removeUserFromEvents(@Param("projectId") UUID projectId, @Param("memberId") String memberId);
}