package com.hust.openerp.taskmanagement.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hust.openerp.taskmanagement.entity.MeetingPlanUser;
import com.hust.openerp.taskmanagement.entity.MeetingPlanUser.MeetingPlanUserId;
import com.hust.openerp.taskmanagement.entity.User;

@Repository
public interface MeetingPlanUserRepository extends JpaRepository<MeetingPlanUser, MeetingPlanUserId> {
	
	@Query("SELECT u FROM User u JOIN MeetingPlanUser mpu ON u.id = mpu.userId WHERE mpu.planId = :planId")
    List<User> findUsersByPlanId(@Param("planId") UUID planId);
    
    boolean existsByUserIdAndPlanId(String userId, UUID planId);

	List<MeetingPlanUser> findByPlanId(UUID planId);
	
	List<MeetingPlanUser> findByMeetingPlanIdAndSessionId(UUID planId, UUID sessionId);
	
	MeetingPlanUser findByPlanIdAndUserId(UUID planId, String userId);

}

