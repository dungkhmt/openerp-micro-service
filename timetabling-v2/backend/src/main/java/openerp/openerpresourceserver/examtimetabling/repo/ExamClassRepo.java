package openerp.openerpresourceserver.examtimetabling.repo;

import openerp.openerpresourceserver.examtimetabling.entity.ExamClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExamClassRepo extends JpaRepository<ExamClass, String> {
}
