package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.request.PagingRequest;
import openerp.openerpresourceserver.dto.request.position.PositionQueryRequest;
import openerp.openerpresourceserver.dto.request.position.PositionRequest;
import openerp.openerpresourceserver.dto.response.position.PositionResponse;
import openerp.openerpresourceserver.entity.Employee;
import openerp.openerpresourceserver.entity.Position;
import openerp.openerpresourceserver.exception.BadRequestException;
import openerp.openerpresourceserver.exception.NotFoundException;
import org.springframework.data.domain.Page;

public interface PositionService {
    Page<PositionResponse> getPositionsByProperties(PositionQueryRequest dto, PagingRequest pagingRequest);
    PositionResponse getPositionById(Long id) throws NotFoundException;
    Position addPosition(PositionRequest dto) throws BadRequestException;
    Position updatePosition(PositionRequest dto) throws BadRequestException, NotFoundException;
    Position deletePosition(Long id) throws NotFoundException;
    Employee assignPositionToEmployee(Long employeeId, Long positionId) throws NotFoundException;

}