package openerp.openerpresourceserver.controller;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.service.SyncAttendanceService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/attendance")
public class AdminSyncAttendanceController {
    private final SyncAttendanceService syncAttendanceService;

    @PutMapping("/sync")
    public void syncAttendance(@RequestParam(name = "start") LocalDate startDate,
                               @RequestParam(name = "end", required = false) LocalDate endDate,
                               @RequestParam(name = "attendance_ids", required = false) List<Integer> attendanceIds,
                               @RequestParam(name = "overwrite", required = false, defaultValue = "false") Boolean overwrite
    ) {
        syncAttendanceService.syncAttendanceByRange(startDate, endDate, attendanceIds, overwrite);
    }
}
