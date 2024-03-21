package com.hust.openerp.taskmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hust.openerp.taskmanagement.entity.TaskLogDetail;

@Repository
public interface TaskLogDetailRepository extends JpaRepository<TaskLogDetail, Long> {

}
