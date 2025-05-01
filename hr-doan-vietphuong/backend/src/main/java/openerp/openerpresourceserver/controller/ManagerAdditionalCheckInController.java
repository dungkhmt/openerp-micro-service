package openerp.openerpresourceserver.controller;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.request.PagingRequest;
import openerp.openerpresourceserver.dto.request.additionalCheckIn.AdditionalCheckInQueryRequest;
import openerp.openerpresourceserver.dto.request.additionalCheckIn.ManagerAdditionalCheckInRequest;
import openerp.openerpresourceserver.dto.response.Result;
import openerp.openerpresourceserver.dto.response.ResultMeta;
import openerp.openerpresourceserver.dto.response.additionalCheckIn.AdditionalCheckInResponse;
import openerp.openerpresourceserver.service.AdditionalCheckInService;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/manager/additional-checkins")
@Validated
public class ManagerAdditionalCheckInController {
    private final AdditionalCheckInService additionalCheckInService;

    @GetMapping
    public Result getAdditionalCheckInByProperties(AdditionalCheckInQueryRequest dto, PagingRequest pagingRequest) {
        Page<AdditionalCheckInResponse> page = additionalCheckInService.getAdditionalCheckInByProperties(dto, pagingRequest);
        return Result.ok(page.getContent(), ResultMeta.of(page));
    }

    @PutMapping("/approve")
    public Result approveAdditionalCheckIns(@RequestBody ManagerAdditionalCheckInRequest dto) {
        return Result.ok(additionalCheckInService.approveAdditionalCheckins(dto));
    }

    @PutMapping("/reject")
    public Result rejectAdditionalCheckIns(@RequestBody ManagerAdditionalCheckInRequest dto) {
        return Result.ok(additionalCheckInService.rejectAdditionalCheckins(dto));
    }
}
