package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.repository;

import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.DepartmentEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DepartmentRepo extends IBaseRepository<DepartmentEntity, String>  {
    boolean existsByDepartmentName(String name);

    List<DepartmentEntity> findByDepartmentCodeIn(List<String> codes);

    @Query("SELECT MAX(d.departmentCode) FROM DepartmentEntity d WHERE d.departmentCode LIKE CONCAT(:prefix, '%')")
    String findMaxDepartmentCode(@Param("prefix") String prefix);

}

