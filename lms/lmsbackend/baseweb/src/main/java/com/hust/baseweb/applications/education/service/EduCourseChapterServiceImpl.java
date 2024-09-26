package com.hust.baseweb.applications.education.service;

import com.hust.baseweb.applications.education.entity.EduCourse;
import com.hust.baseweb.applications.education.entity.EduCourseChapter;
import com.hust.baseweb.applications.education.model.EduCourseChapterModelCreate;
import com.hust.baseweb.applications.education.repo.EduCourseChapterRepo;
import com.hust.baseweb.applications.education.repo.EduCourseRepo;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Log4j2
@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class EduCourseChapterServiceImpl implements EduCourseChapterService {

    private EduCourseChapterRepo eduCourseChapterRepo;
    private EduCourseRepo eduCourseRepo;

    @Override
    public EduCourseChapter save(EduCourseChapterModelCreate eduCourseChapterModelCreate) {
        EduCourseChapter eduCourseChapter = new EduCourseChapter();
        EduCourse course = eduCourseRepo.findById(eduCourseChapterModelCreate.getCourseId()).orElse(null);
        eduCourseChapter.setChapterName(eduCourseChapterModelCreate.getChapterName());
        eduCourseChapter.setEduCourse(course);
        eduCourseChapter = eduCourseChapterRepo.save(eduCourseChapter);
        return eduCourseChapter;
    }

    @Override
    public List<EduCourseChapter> findAll() {
        return eduCourseChapterRepo.findAll();
    }

    @Override
    public List<EduCourseChapter> findAllByCourseId(String courseId) {
        EduCourse eduCourse = eduCourseRepo.findById(courseId).orElse(null);
        return eduCourseChapterRepo.findAllByEduCourse(eduCourse);
    }

    @Override
    public String changeOpenCloseChapterStatus(UUID chapterId) {
        EduCourseChapter eduCourseChapter = eduCourseChapterRepo.findById(chapterId).orElse(null);
        if (eduCourseChapter.getStatusId().equals(EduCourseChapter.STATUS_PRIVATE)) {
            eduCourseChapter.setStatusId(EduCourseChapter.STATUS_PUBLIC);
        } else {
            eduCourseChapter.setStatusId(EduCourseChapter.STATUS_PRIVATE);
        }
        eduCourseChapter = eduCourseChapterRepo.save(eduCourseChapter);
        return eduCourseChapter.getStatusId();
    }
}
