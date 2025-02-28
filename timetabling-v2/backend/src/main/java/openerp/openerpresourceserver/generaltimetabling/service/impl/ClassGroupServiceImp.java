package openerp.openerpresourceserver.generaltimetabling.service.impl;

import openerp.openerpresourceserver.generaltimetabling.model.dto.request.ClassGroupSummary;
import openerp.openerpresourceserver.generaltimetabling.service.ClassGroupService;

import java.util.List;

public class ClassGroupServiceImp implements ClassGroupService {

    @Override
    public List<ClassGroupSummary> getAllClassGroup() {
        return List.of();
    }

    @Override
    public void addClassGroup(Long classId, List<Long> groupIds) {

    }

    @Override
    public void deleteClassGroup(Long classId, List<Long> groupIds) {

    }
}
