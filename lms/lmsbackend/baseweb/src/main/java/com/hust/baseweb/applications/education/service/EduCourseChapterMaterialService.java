package com.hust.baseweb.applications.education.service;

import com.hust.baseweb.applications.education.content.Video;
import com.hust.baseweb.applications.education.entity.EduCourseChapterMaterial;
import com.hust.baseweb.applications.education.model.EduCourseChapterMaterialModelCreate;

import java.util.List;
import java.util.UUID;

public interface EduCourseChapterMaterialService {

    EduCourseChapterMaterial save(
        EduCourseChapterMaterialModelCreate eduCourseChapterMaterialModelCreate,
        Video video
    );

    EduCourseChapterMaterial saveSlide(
        EduCourseChapterMaterialModelCreate eduCourseChapterMaterialModelCreate,
        String stringIdList
    );

    List<EduCourseChapterMaterial> findAll();

    List<EduCourseChapterMaterial> findAllByChapterId(UUID chapterId);

    EduCourseChapterMaterial findById(UUID eduCourseChapterMaterialId);

    EduCourseChapterMaterial updateMaterial(
        UUID eduCourseChapterMaterialId,
        String eduCourseMaterialName,
        String eduCourseMaterialType,
        String slideId,
        UUID sourceId
    );
}
