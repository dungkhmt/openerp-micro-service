package com.hust.openerp.taskmanagement.repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.hust.openerp.taskmanagement.entity.MeetingPlan;

@Repository
public interface MeetingPlanRepository extends JpaRepository<MeetingPlan, UUID>, JpaSpecificationExecutor<MeetingPlan> {
	List<MeetingPlan> findByStatusIdAndRegistrationDeadlineBefore(String statusId, Date date);

	List<MeetingPlan> findByStatusId(String statusId);
	
	@Modifying
    @Transactional
    @Query("UPDATE MeetingPlan m SET m.statusId = :statusId WHERE m.id = :planId")
    void updateStatus(@Param("planId") UUID planId, @Param("statusId") String statusId);

}