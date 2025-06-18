package com.hust.openerp.taskmanagement.repository;

import com.hust.openerp.taskmanagement.entity.MeetingPlanUser;
import com.hust.openerp.taskmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MeetingPlanUserRepository extends JpaRepository<MeetingPlanUser, MeetingPlanUser.MeetingPlanUserId> {

    @Query("SELECT mpu.user FROM MeetingPlanUser mpu  WHERE mpu.meetingPlan.id = :planId")
    List<User> findUsersByPlanId(@Param("planId") UUID planId);

    boolean existsByUserIdAndPlanId(String userId, UUID planId);

    List<MeetingPlanUser> findByPlanId(UUID planId);

    List<MeetingPlanUser> findByMeetingPlanIdAndSessionId(UUID planId, UUID sessionId);

    MeetingPlanUser findByPlanIdAndUserId(UUID planId, String userId);

    @Query("SELECT m FROM MeetingPlanUser m JOIN FETCH m.meetingSession WHERE m.user.id = :userId AND m.meetingPlan.id IN :planIds")
    List<MeetingPlanUser> findAssignmentsForUserInPlans(@Param("userId") String userId, @Param("planIds") List<UUID> planIds);

    @Query("SELECT m.meetingPlan.id, m.user FROM MeetingPlanUser m WHERE m.meetingPlan.id IN :planIds")
    List<Object[]> findAllUsersByPlanIds(@Param("planIds") List<UUID> planIds);
}

