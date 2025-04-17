package openerp.openerpresourceserver.controller;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.request.PagingRequest;
import openerp.openerpresourceserver.dto.request.position.PositionQueryRequest;
import openerp.openerpresourceserver.dto.request.position.PositionRequest;
import openerp.openerpresourceserver.dto.response.Result;
import openerp.openerpresourceserver.dto.response.ResultMeta;
import openerp.openerpresourceserver.dto.response.position.PositionResponse;
import openerp.openerpresourceserver.exception.BadRequestException;
import openerp.openerpresourceserver.exception.NotFoundException;
import openerp.openerpresourceserver.service.PositionService;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/positions")
@Validated
public class AdminPositionController {
    private final PositionService positionService;

    @GetMapping
    public Result getPositionsByProperties(PositionQueryRequest dto, PagingRequest pagingRequest) {
        Page<PositionResponse> page = positionService.getPositionsByProperties(dto, pagingRequest);
        return Result.ok(page.getContent(), ResultMeta.of(page));
    }

    @GetMapping("/{id}")
    public Result getPositionById(@PathVariable Long id) throws NotFoundException {
        return Result.ok(positionService.getPositionById(id));
    }

    @PostMapping
    public Result addPosition(@RequestBody PositionRequest dto) throws BadRequestException {
        return Result.ok(positionService.addPosition(dto));
    }

    @PutMapping
    public Result updatePosition(@RequestBody PositionRequest dto) throws BadRequestException, NotFoundException {
        return Result.ok(positionService.updatePosition(dto));
    }

    @DeleteMapping("/{id}")
    public Result deletePosition(@PathVariable Long id) throws NotFoundException {
        return Result.ok(positionService.deletePosition(id));
    }

    @PostMapping("/assign")
    public Result assignPositionToEmployee(@RequestParam Long employeeId, @RequestParam Long positionId) throws NotFoundException {
        return Result.ok(positionService.assignPositionToEmployee(employeeId, positionId));
    }
}