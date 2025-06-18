package com.hust.openerp.taskmanagement.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hust.openerp.taskmanagement.entity.UserSkill;

@Repository
public interface UserSkillRepository extends JpaRepository<UserSkill, UserSkill.UserSkillId> {

	@Query("SELECT us FROM UserSkill us JOIN FETCH us.skill WHERE us.userId = :userId")
	List<UserSkill> findByUserIdWithSkill(@Param("userId") String userId);
    
    void deleteByUserId(String userId);
    
    void deleteBySkillId(UUID skillId);
}
