package openerp.openerpresourceserver.mapper;
import openerp.openerpresourceserver.dto.OrderRequestDto;
import openerp.openerpresourceserver.dto.OrderResponseDto;
import openerp.openerpresourceserver.dto.OrderSummaryDTO;
import openerp.openerpresourceserver.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    OrderResponseDto orderToOrderDto(Order order);


    Order orderDtoToOrder (OrderRequestDto orderRequestDto);

}
