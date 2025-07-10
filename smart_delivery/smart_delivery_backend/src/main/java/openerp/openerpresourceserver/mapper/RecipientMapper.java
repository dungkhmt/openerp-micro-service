package openerp.openerpresourceserver.mapper;

import openerp.openerpresourceserver.dto.RecipientDto;
import openerp.openerpresourceserver.dto.SenderDto;
import openerp.openerpresourceserver.entity.Recipient;
import openerp.openerpresourceserver.entity.Sender;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RecipientMapper {
    RecipientMapper INSTANCE = Mappers.getMapper(RecipientMapper.class);

    RecipientDto recipientToRecipientDto(Recipient recipient);

    Recipient recipientDtoToRecipient(RecipientDto recipientDto);

}
