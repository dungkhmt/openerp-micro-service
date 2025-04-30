package openerp.openerpresourceserver.service;

import jakarta.mail.MessagingException;
import openerp.openerpresourceserver.dto.request.PagingRequest;
import openerp.openerpresourceserver.dto.request.absence.AbsenceQueryRequest;
import openerp.openerpresourceserver.dto.request.absence.AbsenceRequest;
import openerp.openerpresourceserver.dto.response.absence.AbsenceResponse;
import openerp.openerpresourceserver.entity.AbsenceType;
import openerp.openerpresourceserver.exception.BadRequestException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AbsenceService {
    Page<AbsenceResponse> getAbsencesByUserLogin(AbsenceQueryRequest dto, PagingRequest pagingRequest);

    List<AbsenceType> getAbsencesTypes();

    AbsenceResponse addAbsence(AbsenceRequest dto) throws MessagingException, BadRequestException;
}
