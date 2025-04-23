package openerp.openerpresourceserver.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.request.PagingRequest;
import openerp.openerpresourceserver.dto.request.absenceType.AbsenceTypeQueryRequest;
import openerp.openerpresourceserver.dto.request.absenceType.AbsenceTypeRequest;
import openerp.openerpresourceserver.dto.response.Result;
import openerp.openerpresourceserver.dto.response.ResultMeta;
import openerp.openerpresourceserver.entity.AbsenceType;
import openerp.openerpresourceserver.exception.BadRequestException;
import openerp.openerpresourceserver.service.AbsenceTypeService;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/absence-types")
@Validated
public class AdminAbsenceTypeController {
    private final AbsenceTypeService absenceTypeService;

    @GetMapping
    public Result getAbsenceTypeByProperties(AbsenceTypeQueryRequest dto, PagingRequest pagingRequest) {
        Page<AbsenceType> page = absenceTypeService.getAbsenceTypeByProperties(dto, pagingRequest);
        return Result.ok(page.getContent(), ResultMeta.of(page));
    }

    @GetMapping("/{id}")
    public Result getAbsenceTypeById(@PathVariable Long id) {
        return Result.ok(absenceTypeService.getAbsenceTypeById(id));
    }

    @PostMapping
    public Result addAbsenceType(@RequestBody @Valid AbsenceTypeRequest dto) throws BadRequestException {
        return Result.ok(absenceTypeService.addAbsenceType(dto));
    }

    @PutMapping
    public Result updateAbsenceType(@RequestBody @Valid AbsenceTypeRequest dto) throws BadRequestException {
        return Result.ok(absenceTypeService.updateAbsenceType(dto));
    }

    @DeleteMapping
    public Result deleteAbsenceTypes(@RequestBody List<Long> absenceTypeIdList) {
        return Result.ok(absenceTypeService.deleteAbsenceTypes(absenceTypeIdList));
    }
}

