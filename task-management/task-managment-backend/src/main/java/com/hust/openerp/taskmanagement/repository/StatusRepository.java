package com.hust.openerp.taskmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.google.common.base.Optional;
import com.hust.openerp.taskmanagement.entity.Status;

@Repository
public interface StatusRepository extends JpaRepository<Status, String> {

	List<Status> findAllByOrderByCreatedStampDesc();
	
	Optional<Status> findByStatusCode(String code);

	Optional<Status> findByDescription(String description);
	
	Optional<Status> findByStatusId(String id);

}
