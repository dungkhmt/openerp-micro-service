package openerp.openerpresourceserver.generaltimetabling.service;

import openerp.openerpresourceserver.generaltimetabling.model.dto.request.ClassGroupSummary;

import java.util.List;

public interface ClassGroupService {

    List<ClassGroupSummary> getAllClassGroup();

    void addClassGroup(Long classId, List<Long> groupIds);

    void deleteClassGroup(Long classId, List<Long> groupIds);
}

