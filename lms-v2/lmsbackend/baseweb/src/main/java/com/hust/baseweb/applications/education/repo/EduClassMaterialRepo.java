package com.hust.baseweb.applications.education.repo;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hust.baseweb.applications.education.entity.EduClass;
import com.hust.baseweb.applications.education.entity.EduClassMaterial;
import com.hust.baseweb.applications.education.entity.EduClassMaterialId;

@Repository
public interface EduClassMaterialRepo extends JpaRepository<EduClassMaterial, EduClassMaterialId> {
    @Autowired
    public List<EduClassMaterial> findAllByClassId(UUID eduClassId);
    @Autowired
    public List<EduClassMaterial> findAllByClassIdAndChapterId(UUID eduClassId, UUID eduChapterId);
} 
