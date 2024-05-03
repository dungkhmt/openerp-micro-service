package openerp.openerpresourceserver.generaltimetabling.mapper;

import openerp.openerpresourceserver.generaltimetabling.model.dto.request.GroupDto;
import openerp.openerpresourceserver.generaltimetabling.model.entity.Group;
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
