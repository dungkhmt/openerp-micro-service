package com.hust.baseweb.applications.education.service;


import com.hust.baseweb.applications.education.entity.EduDepartment;
import com.hust.baseweb.applications.education.repo.EduDepartmentRepo;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class EduDepartmentServiceImpl implements EduDepartmentService {

    private EduDepartmentRepo eduDepartmentRepo;

    @Override
    public List<EduDepartment> findAll() {
        return eduDepartmentRepo.findAll();
    }
}
