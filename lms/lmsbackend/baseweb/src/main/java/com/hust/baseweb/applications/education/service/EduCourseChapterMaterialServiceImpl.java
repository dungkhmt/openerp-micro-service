package com.hust.baseweb.applications.education.service;


import com.hust.baseweb.applications.education.content.Video;
import com.hust.baseweb.applications.education.entity.EduCourseChapter;
import com.hust.baseweb.applications.education.entity.EduCourseChapterMaterial;
import com.hust.baseweb.applications.education.model.EduCourseChapterMaterialModelCreate;
import com.hust.baseweb.applications.education.repo.EduCourseChapterMaterialRepo;
import com.hust.baseweb.applications.education.repo.EduCourseChapterRepo;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Log4j2
@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class EduCourseChapterMaterialServiceImpl implements EduCourseChapterMaterialService {

    private EduCourseChapterMaterialRepo eduCourseChapterMaterialRepo;
    private EduCourseChapterRepo eduCourseChapterRepo;

    @Override
    public EduCourseChapterMaterial save(
        EduCourseChapterMaterialModelCreate eduCourseChapterMaterialModelCreate,
        Video video
    ) {
        EduCourseChapterMaterial eduCourseChapterMaterial = new EduCourseChapterMaterial();
        EduCourseChapter eduCourseChapter = eduCourseChapterRepo
            .findById(eduCourseChapterMaterialModelCreate.getChapterId())
            .orElse(null);
        eduCourseChapterMaterial.setEduCourseChapter(eduCourseChapter);
        eduCourseChapterMaterial.setEduCourseMaterialName(eduCourseChapterMaterialModelCreate.getMaterialName());
        eduCourseChapterMaterial.setEduCourseMaterialType(eduCourseChapterMaterialModelCreate.getMaterialType());
        eduCourseChapterMaterial.setSourceId(video.getId());
        eduCourseChapterMaterial = eduCourseChapterMaterialRepo.save(eduCourseChapterMaterial);
        return eduCourseChapterMaterial;
    }

    @Override
    public EduCourseChapterMaterial saveSlide(
        EduCourseChapterMaterialModelCreate eduCourseChapterMaterialModelCreate,
        String stringIdList
    ) {
        EduCourseChapterMaterial eduCourseChapterMaterial = new EduCourseChapterMaterial();
        EduCourseChapter eduCourseChapter = eduCourseChapterRepo
            .findById(eduCourseChapterMaterialModelCreate.getChapterId())
            .orElse(null);
        eduCourseChapterMaterial.setEduCourseChapter(eduCourseChapter);
        eduCourseChapterMaterial.setEduCourseMaterialName(eduCourseChapterMaterialModelCreate.getMaterialName());
        eduCourseChapterMaterial.setEduCourseMaterialType(eduCourseChapterMaterialModelCreate.getMaterialType());
        eduCourseChapterMaterial.setSlideId(stringIdList);
        eduCourseChapterMaterial = eduCourseChapterMaterialRepo.save(eduCourseChapterMaterial);
        return eduCourseChapterMaterial;
    }

    @Override
    public List<EduCourseChapterMaterial> findAll() {
        return eduCourseChapterMaterialRepo.findAll();
    }

    @Override
    public List<EduCourseChapterMaterial> findAllByChapterId(UUID chapterId) {
//        log.info("findAllByChapterId, chapterId = " + chapterId);

        EduCourseChapter eduCourseChapter = eduCourseChapterRepo.findById(chapterId).orElse(null);
        List<EduCourseChapterMaterial> eduCourseChapterMaterials = eduCourseChapterMaterialRepo.findAllByEduCourseChapter(
            eduCourseChapter);
        return eduCourseChapterMaterials;
    }

    @Override
    public EduCourseChapterMaterial findById(UUID eduCourseChapterMaterialId) {
        EduCourseChapterMaterial eduCourseChapterMaterial = eduCourseChapterMaterialRepo
            .findById(eduCourseChapterMaterialId)
            .orElse(null);
        log.info("findById, sourceId = " + eduCourseChapterMaterial.getSourceId());
        return eduCourseChapterMaterial;
    }

    @Override
    public EduCourseChapterMaterial updateMaterial(
        UUID eduCourseChapterMaterialId,
        String eduCourseMaterialName,
        String eduCourseMaterialType,
        String slideId,
        UUID sourceId
    ) {
        EduCourseChapterMaterial eduCourseChapterMaterial = eduCourseChapterMaterialRepo
            .findById(eduCourseChapterMaterialId)
            .orElse(null);
        log.info("findById, sourceId = " + eduCourseChapterMaterial.getSourceId());
        eduCourseChapterMaterial.setEduCourseMaterialName(eduCourseMaterialName);
        eduCourseChapterMaterial.setEduCourseMaterialType(eduCourseMaterialType);
        eduCourseChapterMaterial.setSourceId(sourceId);
        eduCourseChapterMaterial.setSlideId(slideId);

        eduCourseChapterMaterialRepo.save(eduCourseChapterMaterial);
        return eduCourseChapterMaterial;
    }
}
