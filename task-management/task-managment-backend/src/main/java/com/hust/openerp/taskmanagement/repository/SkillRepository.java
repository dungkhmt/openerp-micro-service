package com.hust.openerp.taskmanagement.repository;

import com.hust.openerp.taskmanagement.entity.Skill;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillRepository extends JpaRepository<Skill, UUID> {
	List<Skill> findAllByOrderByCreatedStampDesc();

	Optional<Skill> findByName(String skillName);

    boolean existsByCode(String code);
}
