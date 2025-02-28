package openerp.openerpresourceserver.generaltimetabling.service;

import openerp.openerpresourceserver.generaltimetabling.model.dto.request.ClassGroupSummary;

import java.util.List;

public interface ClassGroupService {

    List<ClassGroupSummary> getAllClassGroup(Long classId);

    void addClassGroup(Long classId, Long groupId);

    void addClassGroups(Long classId, List<Long> groupIds);

    void deleteClassGroup(Long classId, Long groupId);
}

