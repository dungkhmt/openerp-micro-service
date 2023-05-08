package com.hust.baseweb.applications.admin.dataadmin.repo;

import com.hust.baseweb.applications.admin.dataadmin.education.model.ViewClassMaterialLogsOM;
import com.hust.baseweb.applications.education.entity.LogUserLoginCourseChapterMaterial;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface DataAdminLogUserLoginCourseChapterMaterialRepo extends JpaRepository<LogUserLoginCourseChapterMaterial, UUID> {
    @Query(value="select * from log_user_login_course_chapter_material offset ?1 limit ?2",nativeQuery=true)
    List<LogUserLoginCourseChapterMaterial> getPage(int offset, int limit);

    @Query(value="select * from log_user_login_course_chapter_material where user_login_id = ?3 offset ?1 limit ?2",nativeQuery=true)
    List<LogUserLoginCourseChapterMaterial> getPageOfUserLogin(int offset, int limit, String userLoginId);

    @Query(value="select count(*) from log_user_login_course_chapter_material",nativeQuery=true)
    int countTotal();

    @Query(
        nativeQuery = true,
        value = "SELECT course.id courseId, course.course_name courseName, cls.code classCode, cls.semester_id semester, " +
                    "CAST(chapter.chapter_id AS VARCHAR(60)) chapterId, chapter.chapter_name chapterName, " +
                    "material.edu_course_material_name materialName, lg.created_stamp viewAt " +
                "FROM log_user_login_course_chapter_material lg " +
                "INNER JOIN edu_course_chapter_material material " +
                    "ON lg.user_login_id = :studentLoginId " +
                    "AND lg.edu_course_material_id = material.edu_course_material_id " +
                "INNER JOIN edu_course_chapter chapter " +
                    "ON material.chapter_id = chapter.chapter_id " +
                "INNER JOIN edu_course course " +
                    "ON chapter.course_id = course.id " +
                "LEFT JOIN edu_class cls " +
                    "ON lg.edu_class_id = cls.id " +
                "WHERE LOWER(course.id) LIKE CONCAT('%', LOWER(:search), '%') " +
                    "OR LOWER(course.course_name) LIKE CONCAT('%', LOWER(:search), '%') " +
                    "OR LOWER(CAST(cls.code as VARCHAR)) LIKE CONCAT('%', LOWER(:search), '%') " +
                    "OR LOWER(CAST(cls.semester_id as VARCHAR)) LIKE CONCAT('%', LOWER(:search), '%') " +
                    "OR LOWER(chapter.chapter_name) LIKE CONCAT('%', LOWER(:search), '%') " +
                    "OR LOWER(material.edu_course_material_name) LIKE CONCAT('%', LOWER(:search), '%') " +
                    "OR LOWER(CAST(lg.created_stamp as VARCHAR)) LIKE CONCAT('%', LOWER(:search), '%')"

    )
    Page<ViewClassMaterialLogsOM> findViewClassMaterialLogsOfStudent(
        @Param("studentLoginId") String studentLoginId,
        @Param("search") String search,
        Pageable pageable
    );
}
