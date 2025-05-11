package openerp.openerpresourceserver.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import openerp.openerpresourceserver.dto.request.attendanceCycle.AttendanceCycleRequest;
import openerp.openerpresourceserver.dto.response.attendance.AttendanceCycleResponse;
import openerp.openerpresourceserver.entity.Setting;
import openerp.openerpresourceserver.enums.AttendanceCycleEnum;
import openerp.openerpresourceserver.enums.SettingEnum;
import openerp.openerpresourceserver.exception.InternalServerException;
import openerp.openerpresourceserver.repo.SettingRepository;
import openerp.openerpresourceserver.service.SettingService;
import openerp.openerpresourceserver.util.ObjectUtil;
import openerp.openerpresourceserver.util.SecurityUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SettingServiceImpl implements SettingService {
    private final SettingRepository settingRepository;
    private final ObjectMapper objectMapper;


    @Override
    public Setting createAttendanceCycleSetting(AttendanceCycleRequest request) {
        try {
            Setting setting = settingRepository.findById(SettingEnum.ATTENDANCE_CYCLE.name())
                    .orElseGet(() -> Setting.builder()
                            .key(SettingEnum.ATTENDANCE_CYCLE.name())
                            .build());
            String value = request.getType().createJsonSettingValue(request.getEndDay());
            setting.setValue(value);
            setting.setUpdateBy(SecurityUtil.getUserEmail());
            return settingRepository.save(setting);
        } catch (JsonProcessingException e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    @Override
    public AttendanceCycleResponse getAttendanceCycleBySetting(Integer month, Integer year) {
        int yearValue = year == null ? LocalDate.now().getYear() : year;
        int monthValue = month == null ? LocalDate.now().getMonthValue() : month;

        Setting setting = settingRepository.findById(SettingEnum.ATTENDANCE_CYCLE.name())
                .orElse(null);
        if (setting == null)
            return AttendanceCycleEnum.MONTHLY.getAttendanceDateRange(
                    null,
                    monthValue,
                    yearValue);
        String value = setting.getValue();
        AttendanceDateRangeMapper attendanceDateRangeMapper = null;
        try {
            attendanceDateRangeMapper = objectMapper.readValue(value, AttendanceDateRangeMapper.class);
        } catch (JsonProcessingException e) {
            throw new InternalServerException(e.getMessage());
        }
        return attendanceDateRangeMapper.getType().getAttendanceDateRange(
                attendanceDateRangeMapper.getEndDay(),
                monthValue,
                yearValue);
    }

    @Override
    public Map<String, ObjectUtil.RGBColor> getReportColors() {
        Setting setting = settingRepository.findById(SettingEnum.REPORT_COLORS.name()).get();
        String value = setting.getValue();
        Map<String, ObjectUtil.RGBColor> reportColorMap = null;
        try {
            reportColorMap =
                    objectMapper.readValue(value, new TypeReference<Map<String, ObjectUtil.RGBColor>>() {
                    });
        } catch (JsonProcessingException e) {
            throw new InternalServerException(e.getMessage());
        }
        return reportColorMap;
    }

    @Getter
    @Setter
    static class AttendanceDateRangeMapper {
        private String endDay;
        private AttendanceCycleEnum type;
    }
}
