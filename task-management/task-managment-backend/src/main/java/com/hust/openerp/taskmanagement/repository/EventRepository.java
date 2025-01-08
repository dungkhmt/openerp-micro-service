package com.hust.openerp.taskmanagement.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hust.openerp.taskmanagement.entity.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
	List<Event> findByProjectIdOrderByDueDateDesc(UUID projectId);
}
