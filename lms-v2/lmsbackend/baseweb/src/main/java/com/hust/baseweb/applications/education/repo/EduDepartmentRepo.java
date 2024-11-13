package com.hust.baseweb.applications.education.repo;

import com.hust.baseweb.applications.education.entity.EduDepartment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EduDepartmentRepo extends JpaRepository<EduDepartment, String> {

    //EduDepartment findByDepartmentId(String departmentId);
    EduDepartment save(EduDepartment department);

}
