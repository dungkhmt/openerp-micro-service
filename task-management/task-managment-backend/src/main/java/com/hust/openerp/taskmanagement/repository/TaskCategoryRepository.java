package com.hust.openerp.taskmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hust.openerp.taskmanagement.entity.TaskCategory;

@Repository
public interface TaskCategoryRepository
    extends JpaRepository<TaskCategory, String> {
}
