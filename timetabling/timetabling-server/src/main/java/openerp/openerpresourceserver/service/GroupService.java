package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.model.dto.request.GroupDto;
import openerp.openerpresourceserver.model.entity.Group;

import java.util.List;

public interface GroupService {

    List<Group> getGroup();

    Group create(GroupDto groupDto);

    void updateGroup(GroupDto requestDto);

    void deleteById(Long id);
}
