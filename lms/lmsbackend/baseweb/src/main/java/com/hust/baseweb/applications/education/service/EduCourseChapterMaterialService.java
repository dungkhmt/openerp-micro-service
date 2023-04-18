package com.hust.baseweb.applications.education.service;

import com.hust.baseweb.applications.education.content.Video;
import com.hust.baseweb.applications.education.entity.EduCourseChapterMaterial;
import com.hust.baseweb.applications.education.model.EduCourseChapterMaterialModelCreate;

import java.util.List;
import java.util.UUID;

public interface EduCourseChapterMaterialService {

    public EduCourseChapterMaterial save(
        EduCourseChapterMaterialModelCreate eduCourseChapterMaterialModelCreate,
        Video video
    );

    public EduCourseChapterMaterial saveSlide(
        EduCourseChapterMaterialModelCreate eduCourseChapterMaterialModelCreate,
        String stringIdList
    );

    public List<EduCourseChapterMaterial> findAll();

    public List<EduCourseChapterMaterial> findAllByChapterId(UUID chapterId);

    public EduCourseChapterMaterial findById(UUID eduCourseChapterMaterialId);

    public EduCourseChapterMaterial updateMaterial(UUID eduCourseChapterMaterialId, String eduCourseMaterialName, String eduCourseMaterialType, String slideId, UUID sourceId);
}
