package com.hust.baseweb.applications.education.repo;

import com.hust.baseweb.applications.education.entity.EduCourse;
import com.hust.baseweb.applications.education.entity.EduCourseChapter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EduCourseChapterRepo extends JpaRepository<EduCourseChapter, UUID> {

    EduCourseChapter save(EduCourseChapter eduCourseChapter);

    List<EduCourseChapter> findAllByEduCourse(EduCourse eduCourse);
}
