package openerp.openerpresourceserver.generaltimetabling.repo;

import openerp.openerpresourceserver.generaltimetabling.model.entity.TimeTablingCourse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeTablingCourseRepo extends JpaRepository<TimeTablingCourse, String> {

}
