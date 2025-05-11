package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.request.attendanceCycle.AttendanceCycleRequest;
import openerp.openerpresourceserver.dto.response.attendance.AttendanceCycleResponse;
import openerp.openerpresourceserver.entity.Setting;
import openerp.openerpresourceserver.util.ObjectUtil;

import java.util.Map;

public interface SettingService {
    Setting createAttendanceCycleSetting(AttendanceCycleRequest request);

    AttendanceCycleResponse getAttendanceCycleBySetting(Integer month, Integer year);

    Map<String, ObjectUtil.RGBColor> getReportColors();
}
