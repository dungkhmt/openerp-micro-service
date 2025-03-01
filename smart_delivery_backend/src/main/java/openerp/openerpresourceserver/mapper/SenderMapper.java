package openerp.openerpresourceserver.mapper;

import openerp.openerpresourceserver.dto.SenderDto;
import openerp.openerpresourceserver.entity.Sender;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SenderMapper {
    SenderMapper INSTANCE = Mappers.getMapper(SenderMapper.class);

    SenderDto senderToSenderDto(Sender sender);

    Sender senderDtoToSender(SenderDto senderDto);

}
