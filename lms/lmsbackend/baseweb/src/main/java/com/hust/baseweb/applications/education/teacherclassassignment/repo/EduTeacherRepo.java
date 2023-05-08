package com.hust.baseweb.applications.education.teacherclassassignment.repo;


import com.hust.baseweb.applications.education.teacherclassassignment.entity.EduTeacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EduTeacherRepo extends JpaRepository<EduTeacher, String> {

    @Query(
        "Select t from EduTeacher t where t.teacherName like %:keyword% or t.id like %:keyword% or t.userLoginId like %:keyword%")
    Page<EduTeacher> findAllContain(@Param("keyword") String keyword, Pageable pageable);
    
    Optional<EduTeacher> findByUserLoginId(String userLoginId);
    Optional<EduTeacher> findByTeacherName(String teacherName);
    @Query(value = "select * from teacher t where t.id = :id", nativeQuery = true)
    EduTeacher findByTeacherID(String id);
}
