package com.hust.baseweb.applications.education.service;

import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hust.baseweb.applications.education.entity.EduClass;
import com.hust.baseweb.applications.education.entity.EduClassMaterial;
import com.hust.baseweb.applications.education.entity.EduClassMaterialId;
import com.hust.baseweb.applications.education.entity.EduCourseChapter;
import com.hust.baseweb.applications.education.entity.EduCourseChapterMaterial;
import com.hust.baseweb.applications.education.repo.EduClassMaterialRepo;
import com.hust.baseweb.applications.education.repo.EduCourseChapterMaterialRepo;
import com.hust.baseweb.applications.education.repo.EduCourseChapterRepo;

@Service
public class EduClassMaterialServiceImpl implements EduClassMaterialService {
    @Autowired
    private EduClassMaterialRepo eduClassMaterialRepo; 
    private EduCourseChapterMaterialRepo eduCourseChapterMaterialRepo;
    private EduCourseChapterRepo eduCourseChapterRepo;
    @Override
    public EduClassMaterial update(UUID classId, UUID chapterId, UUID materialId, boolean status){
        EduClassMaterial material = eduClassMaterialRepo.findById(new EduClassMaterialId(classId, chapterId, materialId)).orElse(null);
        if (material != null) {
            material.setStatus(status);
            eduClassMaterialRepo.save(material);
        }
        return material;
    }

    @Override
    public List<EduClassMaterial> getMaterialByClassId(UUID classId){
        // EduClass eduClass = new EduClass();
        // eduClass.setId(classId);
        List<EduClassMaterial> materials = eduClassMaterialRepo.findAllByClassId(classId);
        return materials;
    }

    @Override
    public List<EduClassMaterial> getMaterialByClassIdAndChapterId(UUID classId, UUID chapterId){
        // EduClass eduClass = new EduClass();
        // eduClass.setId(classId);
        List<EduClassMaterial> materials = eduClassMaterialRepo.findAllByClassIdAndChapterId(classId, chapterId);
        return materials;
    }

    @Override
    public List<EduClassMaterial> addCourseMaterialsToClass(EduClass eduClass){
        List<EduClassMaterial> eduClassMaterials = new ArrayList<>();
        List<EduCourseChapter> eduCourseChapters = eduCourseChapterRepo.findAllByEduCourse(eduClass.getEduCourse());
        List<EduCourseChapterMaterial> eduCourseChapterMaterials = new ArrayList<>();
        for (EduCourseChapter eduCourseChapter : eduCourseChapters) {
            List<EduCourseChapterMaterial> chapterMaterials = eduCourseChapterMaterialRepo.findAllByEduCourseChapter(eduCourseChapter);
            eduCourseChapterMaterials.addAll(chapterMaterials); 
        }
        for (EduCourseChapterMaterial eduCourseChapterMaterial : eduCourseChapterMaterials) {
            EduClassMaterial eduClassMaterial = new EduClassMaterial();
            eduClassMaterial.setChapterId(eduCourseChapterMaterial.getEduCourseChapter().getChapterId());
            eduClassMaterial.setClassId(eduClass.getId());
            eduClassMaterial.setMaterialId(eduCourseChapterMaterial.getEduCourseMaterialId());
        }
        return eduClassMaterials;
    }
}
