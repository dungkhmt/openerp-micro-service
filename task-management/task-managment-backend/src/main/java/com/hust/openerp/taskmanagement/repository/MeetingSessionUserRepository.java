package com.hust.openerp.taskmanagement.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.hust.openerp.taskmanagement.entity.MeetingSession;
import com.hust.openerp.taskmanagement.entity.MeetingSessionUser;
import com.hust.openerp.taskmanagement.entity.User;

@Repository
public interface MeetingSessionUserRepository
		extends JpaRepository<MeetingSessionUser, MeetingSessionUser.MeetingSessionUserId> {

	@Query("SELECT ms FROM MeetingSession ms JOIN MeetingSessionUser msu ON ms.id = msu.sessionId "
			+ "WHERE msu.userId = :userId AND ms.planId = :planId ORDER BY ms.startTime")
	List<MeetingSession> findSessionsByUserIdAndPlanId(@Param("userId") String userId, @Param("planId") UUID planId);

	@Query("SELECT msu FROM MeetingSession ms JOIN MeetingSessionUser msu ON ms.id = msu.sessionId "
			+ "WHERE ms.planId = :planId ORDER BY ms.startTime")
	List<MeetingSessionUser> findAllSessionRegistration(@Param("planId") UUID planId);
	
	@Modifying
	@Transactional
	@Query("DELETE FROM MeetingSessionUser msu WHERE msu.userId = :userId AND msu.sessionId IN "
	     + "(SELECT ms.id FROM MeetingSession ms WHERE ms.planId = :planId)")
	void deleteByUserIdAndPlanId(@Param("userId") String userId, @Param("planId") UUID planId);
	
	void deleteBySessionId(UUID sessionId);
	
	void deleteByUserId(String userId);
}
