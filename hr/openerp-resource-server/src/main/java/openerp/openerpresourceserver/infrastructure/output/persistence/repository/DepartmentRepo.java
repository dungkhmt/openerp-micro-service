package openerp.openerpresourceserver.infrastructure.output.persistence.repository;

import openerp.openerpresourceserver.infrastructure.output.persistence.entity.DepartmentEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DepartmentRepo extends IBaseRepository<DepartmentEntity, String>  {
    boolean existsByDepartmentName(String name);

    @Query("SELECT MAX(d.departmentCode) FROM DepartmentEntity d WHERE d.departmentCode LIKE CONCAT(:prefix, '%')")
    String findMaxDepartmentCode(@Param("prefix") String prefix);

}

