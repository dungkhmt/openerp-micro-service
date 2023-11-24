package openerp.openerpresourceserver.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import openerp.openerpresourceserver.common.CommonUtil;
import openerp.openerpresourceserver.model.dto.request.FilterClassOpenedDto;
import openerp.openerpresourceserver.model.dto.request.FilterScheduleDto;
import openerp.openerpresourceserver.model.dto.request.UpdateClassOpenedDto;
import openerp.openerpresourceserver.model.entity.ClassOpened;
import openerp.openerpresourceserver.model.entity.Schedule;
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

    @Autowired
    private EntityManager entityManager;

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

    @Override
    public List<ClassOpened> getByGroupName(String groupName) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        return classOpenedRepo.getAllByGroupName(groupName, sort);
    }

    @Override
    public List<Schedule> searchClassOpened(FilterClassOpenedDto searchDto) {
        StringBuilder jpql = this.getStringBuilder(searchDto);

        Query query = entityManager.createQuery(jpql.toString());

        // Execute the query and return the result list
        return CommonUtil.buildQueryForClassOpened(query, searchDto).getResultList();
    }

    private StringBuilder getStringBuilder(FilterClassOpenedDto searchDto) {
        StringBuilder jpql = new StringBuilder("SELECT s FROM ClassOpened s WHERE 1 = 1");

        return CommonUtil.appendAttributesForClassOpened(jpql, searchDto);
    }
}
