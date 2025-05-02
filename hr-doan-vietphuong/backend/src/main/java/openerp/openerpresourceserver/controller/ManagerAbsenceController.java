package openerp.openerpresourceserver.controller;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.request.PagingRequest;
import openerp.openerpresourceserver.dto.request.absence.AbsenceQueryRequest;
import openerp.openerpresourceserver.dto.request.absence.ManagerAbsenceRequest;
import openerp.openerpresourceserver.dto.response.Result;
import openerp.openerpresourceserver.dto.response.ResultMeta;
import openerp.openerpresourceserver.dto.response.absence.AbsenceResponse;
import openerp.openerpresourceserver.exception.BadRequestException;
import openerp.openerpresourceserver.service.AbsenceService;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/manager/absences")
@Validated
public class ManagerAbsenceController {
    private final AbsenceService absenceService;

    @GetMapping
    public Result getAbsencesByProperties(AbsenceQueryRequest dto, PagingRequest pagingRequest) {
        Page<AbsenceResponse> page = absenceService.getAbsencesByProperties(dto, pagingRequest);
        return Result.ok(page.getContent(), ResultMeta.of(page));
    }

    @PutMapping("/approve")
    public Result approveAbsences(@RequestBody ManagerAbsenceRequest dto) throws BadRequestException {
        return Result.ok(absenceService.approveAbsences(dto));
    }

    @PutMapping("/reject")
    public Result rejectAbsences(@RequestBody ManagerAbsenceRequest dto) {
        return Result.ok(absenceService.rejectAbsences(dto));
    }
}
