package com.hust.baseweb.applications.education.service;

import com.hust.baseweb.applications.education.entity.Semester;
import com.hust.baseweb.applications.education.repo.SemesterRepo;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SemesterServiceImpl implements SemesterService {

    private SemesterRepo semesterRepo;

    @Override
    public List<Semester> findAll() {
        return semesterRepo.findAll();
    }
}
