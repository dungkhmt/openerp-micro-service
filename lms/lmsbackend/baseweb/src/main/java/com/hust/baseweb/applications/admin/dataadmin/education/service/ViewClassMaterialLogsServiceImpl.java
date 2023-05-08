package com.hust.baseweb.applications.admin.dataadmin.education.service;

import com.hust.baseweb.applications.admin.dataadmin.education.model.ViewClassMaterialLogsOM;
import com.hust.baseweb.applications.admin.dataadmin.repo.DataAdminLogUserLoginCourseChapterMaterialRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ViewClassMaterialLogsServiceImpl implements ViewClassMaterialLogsService {

    private final DataAdminLogUserLoginCourseChapterMaterialRepo viewClassMaterialLogsRepo;
    @Override
    public Page<ViewClassMaterialLogsOM> findViewClassMaterialLogsOfStudent(
        String studentLoginId,
        String search,
        Pageable pageable
    ) {
        return viewClassMaterialLogsRepo.findViewClassMaterialLogsOfStudent(
            studentLoginId, search, pageable
        );
    }
}
