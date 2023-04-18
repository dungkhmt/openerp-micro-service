package com.hust.baseweb.applications.education.service;

import com.hust.baseweb.applications.education.report.model.courseparticipation.StudentCourseParticipationModel;
import com.hust.baseweb.entity.UserLogin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface LogUserLoginCourseChapterMaterialService {

    public void logUserLoginMaterial(UserLogin userLogin, UUID eduCourseChapterMaterialId);
    public void logUserLoginMaterialV2(String userId, UUID classId, UUID eduCourseChapterMaterialId);

    public List<StudentCourseParticipationModel> findAllByClassId(UUID classId);
    public Page<StudentCourseParticipationModel> findDataByClassIdAndPage(UUID classId, String userIdPattern, Pageable pageable);
}
