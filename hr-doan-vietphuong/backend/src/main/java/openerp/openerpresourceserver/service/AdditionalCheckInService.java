package openerp.openerpresourceserver.service;

import jakarta.mail.MessagingException;
import openerp.openerpresourceserver.dto.request.PagingRequest;
import openerp.openerpresourceserver.dto.request.additionalCheckIn.AdditionalCheckInQueryRequest;
import openerp.openerpresourceserver.dto.request.additionalCheckIn.AdditionalCheckInRequest;
import openerp.openerpresourceserver.dto.request.additionalCheckIn.ManagerAdditionalCheckInRequest;
import openerp.openerpresourceserver.dto.response.additionalCheckIn.AdditionalCheckInResponse;
import openerp.openerpresourceserver.entity.AbsenceType;
import openerp.openerpresourceserver.exception.BadRequestException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AdditionalCheckInService {
    Page<AdditionalCheckInResponse> getAdditionalCheckInByUserLogin(AdditionalCheckInQueryRequest dto, PagingRequest pagingRequest);

    List<AbsenceType> getAdditionalCheckInTypes();

    AdditionalCheckInResponse addAdditionalCheckIn(AdditionalCheckInRequest dto) throws MessagingException, BadRequestException;

    Page<AdditionalCheckInResponse> getAdditionalCheckInByProperties(AdditionalCheckInQueryRequest dto, PagingRequest pagingRequest);

    List<AdditionalCheckInResponse> approveAdditionalCheckins(ManagerAdditionalCheckInRequest dto);

    List<AdditionalCheckInResponse> rejectAdditionalCheckins(ManagerAdditionalCheckInRequest dto);
}
