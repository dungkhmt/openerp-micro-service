package com.hust.openerp.taskmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hust.openerp.taskmanagement.entity.Skill;

@Repository
public interface SkillRepository extends JpaRepository<Skill, String> {
}
