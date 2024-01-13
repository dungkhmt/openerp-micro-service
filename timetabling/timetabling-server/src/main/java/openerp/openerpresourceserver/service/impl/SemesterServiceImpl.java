package openerp.openerpresourceserver.service.impl;

import openerp.openerpresourceserver.exception.SemesterNotFoundException;
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
    public void updateSemester(SemesterDto requestDto) {
        Long id = requestDto.getId();
        Semester semester = semesterRepo.findById(id).orElse(null);
        if (semester == null) {
            throw new SemesterNotFoundException("Not found semester with ID: " + id);
        }
        semester.setSemester(requestDto.getSemester());
        semester.setDescription(requestDto.getDescription());
        semesterRepo.save(semester);
    }

    @Override
    public Semester create(SemesterDto semesterDto) {
        Semester semester = semesterMapper.mapDtoToEntity(semesterDto);
        semesterRepo.save(semester);
        return semester;
    }

    @Override
    public void deleteById(Long id) {
        semesterRepo.deleteById(id);
    }
}
