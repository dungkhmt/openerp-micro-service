package openerp.openerpresourceserver.mapper;

import openerp.openerpresourceserver.model.dto.request.GroupDto;
import openerp.openerpresourceserver.model.entity.Group;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GroupMapper {

    @Autowired
    private ModelMapper modelMapper;

    public Group mapDtoToEntity(GroupDto groupDto) {
        return modelMapper.map(groupDto, Group.class);
    }

    public GroupDto mapEntityToDto(Group group) {
        return modelMapper.map(group, GroupDto.class);
    }
}
