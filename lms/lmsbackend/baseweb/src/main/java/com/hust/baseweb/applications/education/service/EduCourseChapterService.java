package com.hust.baseweb.applications.education.service;

import com.hust.baseweb.applications.education.entity.EduCourseChapter;
import com.hust.baseweb.applications.education.model.EduCourseChapterModelCreate;

import java.util.List;
import java.util.UUID;

public interface EduCourseChapterService {

    public EduCourseChapter save(EduCourseChapterModelCreate eduCourseChapterModelCreate);

    public List<EduCourseChapter> findAll();

    public List<EduCourseChapter> findAllByCourseId(String courseId);

    public String changeOpenCloseChapterStatus(UUID chapterId);
}
