package openerp.openerpresourceserver.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.request.attendanceCycle.AttendanceCycleRequest;
import openerp.openerpresourceserver.dto.response.Result;
import openerp.openerpresourceserver.service.SettingService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/setting")
public class AdminSettingController {
    private final SettingService settingService;

    @PutMapping("/attendance-cycle")
    public Result createAttendanceCycleSetting(@Valid @RequestBody AttendanceCycleRequest request) {
        return Result.ok(settingService.createAttendanceCycleSetting(request));
    }
}
