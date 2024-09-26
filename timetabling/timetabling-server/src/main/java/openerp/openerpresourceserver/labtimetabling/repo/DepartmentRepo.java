package openerp.openerpresourceserver.labtimetabling.repo;

import openerp.openerpresourceserver.labtimetabling.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepo extends JpaRepository<Department, Long> {
}
