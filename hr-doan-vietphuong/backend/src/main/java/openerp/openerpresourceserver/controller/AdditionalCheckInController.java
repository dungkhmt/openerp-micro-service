package openerp.openerpresourceserver.controller;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.request.PagingRequest;
import openerp.openerpresourceserver.dto.request.additionalCheckIn.AdditionalCheckInQueryRequest;
import openerp.openerpresourceserver.dto.request.additionalCheckIn.AdditionalCheckInRequest;
import openerp.openerpresourceserver.dto.response.Result;
import openerp.openerpresourceserver.dto.response.ResultMeta;
import openerp.openerpresourceserver.dto.response.additionalCheckIn.AdditionalCheckInResponse;
import openerp.openerpresourceserver.exception.BadRequestException;
import openerp.openerpresourceserver.service.AdditionalCheckInService;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/additional-checkins")
@Validated
public class AdditionalCheckInController {
    private final AdditionalCheckInService additionalCheckInService;

    @GetMapping
    public Result getUserAdditionalCheckIns(AdditionalCheckInQueryRequest dto, PagingRequest pagingRequest) {
        Page<AdditionalCheckInResponse> page = additionalCheckInService.getAdditionalCheckInByUserLogin(dto, pagingRequest);
        return Result.ok(page.getContent(), ResultMeta.of(page));
    }

    @GetMapping("/types")
    public Result getAdditionalCheckInTypes() {
        return Result.ok(additionalCheckInService.getAdditionalCheckInTypes());
    }

    @PostMapping
    public Result addAdditionalCheckin(@RequestBody @Valid AdditionalCheckInRequest dto) throws MessagingException, BadRequestException {
        return Result.ok(additionalCheckInService.addAdditionalCheckIn(dto));
    }
}
