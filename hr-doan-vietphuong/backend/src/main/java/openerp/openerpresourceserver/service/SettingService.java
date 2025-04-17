package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.request.attendanceCycle.AttendanceCycleRequest;
import openerp.openerpresourceserver.dto.response.attendance.AttendanceCycleResponse;
import openerp.openerpresourceserver.entity.Setting;

public interface SettingService {
    Setting createAttendanceCycleSetting(AttendanceCycleRequest request);

    AttendanceCycleResponse getAttendanceCycleBySetting(Integer month, Integer year);
}
