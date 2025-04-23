package openerp.openerpresourceserver.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.request.PagingRequest;
import openerp.openerpresourceserver.dto.request.holiday.HolidayQueryRequest;
import openerp.openerpresourceserver.dto.request.holiday.HolidayRequest;
import openerp.openerpresourceserver.dto.response.Result;
import openerp.openerpresourceserver.dto.response.ResultMeta;
import openerp.openerpresourceserver.entity.Holiday;
import openerp.openerpresourceserver.exception.BadRequestException;
import openerp.openerpresourceserver.service.HolidayService;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/holidays")
@RequiredArgsConstructor
@Validated
public class AdminHolidayController {
    private final HolidayService holidayService;

    @GetMapping
    public Result getHolidaysByProperties(HolidayQueryRequest dto, PagingRequest pagingRequest) {
        Page<Holiday> page = holidayService.getHolidaysByProperties(dto, pagingRequest);
        return Result.ok(page.getContent(), ResultMeta.of(page));
    }

    @GetMapping("/{id}")
    public Result getHolidayById(@PathVariable("id") Long id) {
        return Result.ok(holidayService.getHolidayById(id));
    }

    @PostMapping
    public Result createHoliday(@RequestBody @Valid HolidayRequest dto) throws BadRequestException {
        return Result.ok(holidayService.createHoliday(dto));
    }

    @PutMapping
    public Result editHoliday(@RequestBody @Valid  HolidayRequest dto) throws BadRequestException {
        return Result.ok(holidayService.editHoliday(dto));
    }

    @DeleteMapping
    public Result deleteHolidays(@RequestBody List<Long> idList) {
        return Result.ok(holidayService.deleteHolidays(idList));
    }
}

