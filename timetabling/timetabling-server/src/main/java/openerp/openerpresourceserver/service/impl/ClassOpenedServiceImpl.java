package openerp.openerpresourceserver.service.impl;

import openerp.openerpresourceserver.model.dto.request.UpdateClassOpenedDto;
import openerp.openerpresourceserver.model.entity.ClassOpened;
import openerp.openerpresourceserver.repo.ClassOpenedRepo;
import openerp.openerpresourceserver.service.ClassOpenedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassOpenedServiceImpl implements ClassOpenedService {

    @Autowired
    private ClassOpenedRepo classOpenedRepo;

    @Override
    public List<ClassOpened> getAll() {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        return classOpenedRepo.findAll(sort);
    }

    @Override
    public List<ClassOpened> updateClassOpenedList(UpdateClassOpenedDto requestDto) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        List<ClassOpened> classOpenedList = classOpenedRepo.getAllByIdIn(requestDto.getIds(), sort);
        String groupName = requestDto.getGroupName();
        classOpenedList.forEach(el -> {
            el.setGroupName(groupName);
        });
        classOpenedRepo.saveAll(classOpenedList);
        return classOpenedRepo.findAllById(requestDto.getIds());
    }

    @Override
    public List<ClassOpened> getBySemester(String semester) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        return classOpenedRepo.getAllBySemester(semester, sort);
    }
}
