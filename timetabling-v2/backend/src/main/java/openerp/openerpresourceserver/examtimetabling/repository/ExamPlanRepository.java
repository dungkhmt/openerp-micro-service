package openerp.openerpresourceserver.examtimetabling.repository;

import openerp.openerpresourceserver.examtimetabling.entity.ExamPlan;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamPlanRepository extends JpaRepository<ExamPlan, UUID> {
}
