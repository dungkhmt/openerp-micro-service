package com.hust.openerp.taskmanagement.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hust.openerp.taskmanagement.entity.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
	List<Event> findByProjectIdOrderByDueDateDesc(UUID projectId);
	
	@Query("SELECT e.id FROM Event e WHERE e.project.id = :projectId")
	List<UUID> findEventIdsByProjectId(@Param("projectId") UUID projectId);

}
