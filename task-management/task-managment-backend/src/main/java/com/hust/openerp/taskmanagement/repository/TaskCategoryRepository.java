package com.hust.openerp.taskmanagement.repository;

import com.hust.openerp.taskmanagement.entity.TaskCategory;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskCategoryRepository
    extends JpaRepository<TaskCategory, String> {

	Optional<TaskCategory> findByCategoryName(String name);
}
