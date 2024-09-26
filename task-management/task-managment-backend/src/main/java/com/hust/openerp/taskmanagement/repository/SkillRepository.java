package com.hust.openerp.taskmanagement.repository;

import com.hust.openerp.taskmanagement.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillRepository extends JpaRepository<Skill, String> {
}
