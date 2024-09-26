package com.hust.baseweb.applications.education.repo;

import com.hust.baseweb.applications.education.entity.EduCourseChapter;
import com.hust.baseweb.applications.education.entity.EduCourseChapterMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EduCourseChapterMaterialRepo extends JpaRepository<EduCourseChapterMaterial, UUID> {

    public EduCourseChapterMaterial save(EduCourseChapterMaterial eduCourseChapterMaterial);

    public List<EduCourseChapterMaterial> findAllByEduCourseChapter(EduCourseChapter eduCourseChapter);
}
