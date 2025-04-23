package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.request.PagingRequest;
import openerp.openerpresourceserver.dto.request.absenceType.AbsenceTypeQueryRequest;
import openerp.openerpresourceserver.dto.request.absenceType.AbsenceTypeRequest;
import openerp.openerpresourceserver.entity.AbsenceType;
import openerp.openerpresourceserver.exception.BadRequestException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AbsenceTypeService {
    Page<AbsenceType> getAbsenceTypeByProperties(AbsenceTypeQueryRequest dto, PagingRequest pagingRequest);

    AbsenceType getAbsenceTypeById(Long id);

    AbsenceType addAbsenceType(AbsenceTypeRequest dto) throws BadRequestException;

    AbsenceType updateAbsenceType(AbsenceTypeRequest dto) throws BadRequestException;

    List<AbsenceType> deleteAbsenceTypes(List<Long> absenceTypeIdList);
}

