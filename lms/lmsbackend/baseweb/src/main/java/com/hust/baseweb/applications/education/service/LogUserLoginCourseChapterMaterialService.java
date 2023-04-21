package com.hust.baseweb.applications.education.service;

import com.hust.baseweb.applications.education.report.model.courseparticipation.StudentCourseParticipationModel;
import com.hust.baseweb.entity.UserLogin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface LogUserLoginCourseChapterMaterialService {

    void logUserLoginMaterial(UserLogin userLogin, UUID eduCourseChapterMaterialId);

    void logUserLoginMaterialV2(String userId, UUID classId, UUID eduCourseChapterMaterialId);

    List<StudentCourseParticipationModel> findAllByClassId(UUID classId);

    Page<StudentCourseParticipationModel> findDataByClassIdAndPage(
        UUID classId,
        String userIdPattern,
        Pageable pageable
    );
}
