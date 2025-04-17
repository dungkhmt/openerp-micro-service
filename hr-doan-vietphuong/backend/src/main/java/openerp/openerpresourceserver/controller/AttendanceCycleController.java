package openerp.openerpresourceserver.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.response.Result;
import openerp.openerpresourceserver.service.SettingService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/attendance-cycle")
@Validated
public class AttendanceCycleController {
    private final SettingService settingService;

    @GetMapping
    public Result getAttendanceDateRangeBySetting(@RequestParam(required = false)
                                                  @Min(value = 1)
                                                  @Max(value = 12)
                                                  Integer month,
                                                  @RequestParam(required = false)
                                                  @Min(value = 1)
                                                  Integer year) {
        return Result.ok(settingService.getAttendanceCycleBySetting(month, year));
    }
}
