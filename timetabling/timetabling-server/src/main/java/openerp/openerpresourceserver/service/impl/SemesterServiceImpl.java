package openerp.openerpresourceserver.service.impl;

import openerp.openerpresourceserver.mapper.SemesterMapper;
import openerp.openerpresourceserver.model.dto.request.SemesterDto;
import openerp.openerpresourceserver.model.entity.Semester;
import openerp.openerpresourceserver.repo.SemesterRepo;
import openerp.openerpresourceserver.service.SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SemesterServiceImpl implements SemesterService {

    @Autowired
    private SemesterRepo semesterRepo;

    @Autowired
    private SemesterMapper semesterMapper;

    @Override
    public List<Semester> getSemester() {
        return semesterRepo.findAll();
    }

    @Override
    public void updateSemester() {
        List<String> semesterDataList = semesterRepo.getSemester();
        if (!semesterDataList.isEmpty()) {
            semesterRepo.deleteAll();
        }
        List<Semester> semesterList = new ArrayList<>();
        semesterDataList.forEach(el -> {
            Semester semester = Semester.builder()
                    .semester(el)
                    .build();
            semesterList.add(semester);
        });
        semesterRepo.saveAll(semesterList);
    }

    @Override
    public Semester create(SemesterDto semesterDto) {
        Semester semester = semesterMapper.mapDtoToEntity(semesterDto);
        semesterRepo.save(semester);
        return semester;
    }
}
