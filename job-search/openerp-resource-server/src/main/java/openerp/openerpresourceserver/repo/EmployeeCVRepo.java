package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.Education;
import openerp.openerpresourceserver.entity.EmployeeCV;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeCVRepo extends JpaRepository<EmployeeCV, Integer> {
    List<EmployeeCV> findByUserId(String userId);
}
