package openerp.openerpresourceserver.generaltimetabling.service;

import openerp.openerpresourceserver.generaltimetabling.model.dto.request.GroupDto;
import openerp.openerpresourceserver.generaltimetabling.model.entity.Group;

import java.util.List;

public interface GroupService {

    List<Group> getGroup();

    Group create(GroupDto groupDto);

    void updateGroup(GroupDto requestDto);

    void deleteById(Long id);
}
