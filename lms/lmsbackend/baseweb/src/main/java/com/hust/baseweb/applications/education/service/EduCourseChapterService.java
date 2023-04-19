package com.hust.baseweb.applications.education.service;

import com.hust.baseweb.applications.education.entity.EduCourseChapter;
import com.hust.baseweb.applications.education.model.EduCourseChapterModelCreate;

import java.util.List;
import java.util.UUID;

public interface EduCourseChapterService {

    EduCourseChapter save(EduCourseChapterModelCreate eduCourseChapterModelCreate);

    List<EduCourseChapter> findAll();

    List<EduCourseChapter> findAllByCourseId(String courseId);

    String changeOpenCloseChapterStatus(UUID chapterId);
}
