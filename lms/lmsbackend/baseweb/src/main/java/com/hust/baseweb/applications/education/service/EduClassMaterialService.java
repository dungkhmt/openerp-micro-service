package com.hust.baseweb.applications.education.service;

import java.util.List;
import java.util.UUID;

import com.hust.baseweb.applications.education.entity.EduClass;
import com.hust.baseweb.applications.education.entity.EduClassMaterial;

public interface EduClassMaterialService {
    EduClassMaterial update(UUID classId, UUID chapterId, UUID materialId, boolean status);
    List<EduClassMaterial> getMaterialByClassId(UUID classId);
    List<EduClassMaterial> getMaterialByClassIdAndChapterId(UUID classId, UUID chapterId);
    List<EduClassMaterial> addCourseMaterialsToClass(EduClass eduClass);
}
