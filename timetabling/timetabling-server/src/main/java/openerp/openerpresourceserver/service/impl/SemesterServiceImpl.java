package openerp.openerpresourceserver.service.impl;

import openerp.openerpresourceserver.exception.SemesterNotFoundException;
import openerp.openerpresourceserver.exception.SemesterUsedException;
import openerp.openerpresourceserver.mapper.SemesterMapper;
import openerp.openerpresourceserver.model.dto.request.SemesterDto;
import openerp.openerpresourceserver.model.entity.ClassOpened;
import openerp.openerpresourceserver.model.entity.Semester;
import openerp.openerpresourceserver.repo.ClassOpenedRepo;
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
    private ClassOpenedRepo classOpenedRepo;

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
            throw new SemesterNotFoundException("Không tìm thấy kỳ học với ID: " + id);
        }
        if (!semester.getSemester().equals(requestDto.getSemester())) {
            List<Semester> existedSemesters = semesterRepo.getSemestersBySemester(requestDto.getSemester());
            if (!existedSemesters.isEmpty()) {
                throw new SemesterUsedException("Kỳ học " + requestDto.getSemester() + " đã tồn tại!");
            }
            List<ClassOpened> classOpenedList = classOpenedRepo.getAllBySemester(semester.getSemester(), null);
            if (!classOpenedList.isEmpty()) {
                throw new SemesterUsedException("Kỳ học " + semester.getSemester() + " đang được sử dụng. Không thể sửa đổi!");
            }
        }
        semester.setSemester(requestDto.getSemester());
        semester.setDescription(requestDto.getDescription());
        semesterRepo.save(semester);
    }

    @Override
    public Semester create(SemesterDto semesterDto) {
        List<Semester> semesterList = semesterRepo.getSemestersBySemester(semesterDto.getSemester());
        if (!semesterList.isEmpty()) {
            throw new SemesterUsedException("Kỳ học " + semesterDto.getSemester() + " đã tồn tại!");
        }
        Semester semester = semesterMapper.mapDtoToEntity(semesterDto);
        semesterRepo.save(semester);
        return semester;
    }

    @Override
    public void deleteById(Long id) {
        Semester semester = semesterRepo.findById(id).orElse(null);
        if (semester == null) {
            throw new SemesterNotFoundException("Không tồn tại học kỳ với ID: " + id);
        }
        List<ClassOpened> classOpenedList = classOpenedRepo.getAllBySemester(semester.getSemester(), null);
        if (!classOpenedList.isEmpty()) {
            throw new SemesterUsedException("Kỳ học đang được sử dụng. Không thể xóa!");
        }
        semesterRepo.deleteById(id);
    }
}
