package com.hust.baseweb.applications.education.service;

import java.util.UUID;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hust.baseweb.applications.education.entity.EduClass;
import com.hust.baseweb.applications.education.entity.EduClassMaterial;
import com.hust.baseweb.applications.education.entity.EduClassMaterialId;
import com.hust.baseweb.applications.education.repo.EduClassMaterialRepo;

@Service
public class EduClassMaterialServiceImpl implements EduClassMaterialService {
    @Autowired
    private EduClassMaterialRepo eduClassMaterialRepo; 
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
}
