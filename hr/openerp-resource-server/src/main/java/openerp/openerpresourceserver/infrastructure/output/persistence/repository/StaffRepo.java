package openerp.openerpresourceserver.infrastructure.output.persistence.repository;

import openerp.openerpresourceserver.infrastructure.output.persistence.entity.StaffEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StaffRepo extends IBaseRepository<StaffEntity, String>  {
    Optional<StaffEntity> findByUser_Id(String userLoginId);

    @Query("SELECT MAX(d.staffCode) FROM StaffEntity d WHERE d.staffCode LIKE CONCAT(:prefix, '%')")
    String findMaxStaffCode(@Param("prefix") String prefix);
}

