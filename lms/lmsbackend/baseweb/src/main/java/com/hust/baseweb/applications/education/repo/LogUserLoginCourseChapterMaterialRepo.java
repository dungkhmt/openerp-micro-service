package com.hust.baseweb.applications.education.repo;

import com.hust.baseweb.applications.education.entity.LogUserLoginCourseChapterMaterial;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface LogUserLoginCourseChapterMaterialRepo extends JpaRepository<LogUserLoginCourseChapterMaterial, UUID> {

    LogUserLoginCourseChapterMaterial save(LogUserLoginCourseChapterMaterial logUserLoginCourseChapterMaterial);

    @Query("SELECT l FROM LogUserLoginCourseChapterMaterial l " +
           "WHERE l.eduClass.id = :classId AND l.userLoginId LIKE %:userLoginIdPattern%")
    Page<LogUserLoginCourseChapterMaterial> findByEduClass_IdAndUserLoginIdContaining(
        @Param("classId") UUID classId,
        @Param("userLoginIdPattern") String userLoginIdPattern,
        Pageable pageable
    );


}
