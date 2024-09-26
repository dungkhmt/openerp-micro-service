package com.hust.baseweb.applications.admin.dataadmin.education.service;

import com.hust.baseweb.applications.admin.dataadmin.education.model.ViewClassMaterialLogsOM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ViewClassMaterialLogsService {

    Page<ViewClassMaterialLogsOM> findViewClassMaterialLogsOfStudent(
        String studentLoginId,
        String search,
        Pageable pageable
    );
}
