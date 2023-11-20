package openerp.openerpresourceserver.service.impl;

import openerp.openerpresourceserver.model.dto.request.UpdateClassOpenedDto;
import openerp.openerpresourceserver.model.entity.ClassOpened;
import openerp.openerpresourceserver.repo.ClassOpenedRepo;
import openerp.openerpresourceserver.service.ClassOpenedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassOpenedServiceImpl implements ClassOpenedService {

    @Autowired
    private ClassOpenedRepo classOpenedRepo;

    @Override
    public List<ClassOpened> getAll() {
        return classOpenedRepo.findAll();
    }

    @Override
    public List<ClassOpened> updateClassOpenedList(UpdateClassOpenedDto requestDto) {
        List<ClassOpened> classOpenedList = classOpenedRepo.getAllByIds(requestDto.getIds());
        String groupName = requestDto.getGroupName();
        classOpenedList.forEach(el -> {
            el.setGroupName(groupName);
        });
        classOpenedRepo.saveAll(classOpenedList);
        return classOpenedRepo.findAllById(requestDto.getIds());
    }
}
