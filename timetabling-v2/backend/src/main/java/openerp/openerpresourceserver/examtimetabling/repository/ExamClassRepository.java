package openerp.openerpresourceserver.examtimetabling.repository;

import openerp.openerpresourceserver.examtimetabling.entity.ExamClass;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamClassRepository extends JpaRepository<ExamClass, String> {
}
