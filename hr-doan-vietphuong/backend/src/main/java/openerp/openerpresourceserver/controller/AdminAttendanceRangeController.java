package openerp.openerpresourceserver.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.request.PagingRequest;
import openerp.openerpresourceserver.dto.request.attendanceRange.AttendanceRangeQueryRequest;
import openerp.openerpresourceserver.dto.request.attendanceRange.AttendanceRangeRequest;
import openerp.openerpresourceserver.dto.response.Result;
import openerp.openerpresourceserver.dto.response.ResultMeta;
import openerp.openerpresourceserver.entity.AttendanceRange;
import openerp.openerpresourceserver.service.AttendanceRangeService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/attendance/range")
public class AdminAttendanceRangeController {
    private final AttendanceRangeService attendanceRangeService;

    @GetMapping
    public Result getAttendanceRanges(AttendanceRangeQueryRequest request, PagingRequest pagingRequest) {
        Page<AttendanceRange> page = attendanceRangeService.getAttendanceRanges(request, pagingRequest);
        return Result.ok(page.getContent(), ResultMeta.of(page));
    }

    @GetMapping("/{id}")
    public Result getAttendanceRangeById(@PathVariable Long id) {
        return Result.ok(attendanceRangeService.getAttendanceRangeById(id));
    }

    @PostMapping
    public Result createAttendanceRange(@Valid @RequestBody AttendanceRangeRequest request) {
        return Result.ok(attendanceRangeService.createAttendanceRange(request));
    }

    @PutMapping
    public Result updateAttendanceRange(@Valid @RequestBody AttendanceRangeRequest request) {
        return Result.ok(attendanceRangeService.updateAttendanceRange(request));
    }

    @DeleteMapping("/{id}")
    public Result deleteAttendanceRange(@PathVariable long id) {
        return Result.ok(attendanceRangeService.deleteAttendanceRange(id));
    }

}
