package openerp.openerpresourceserver.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class ObjectUtil {
    public record RGBColor(int red,
                           int green,
                           int blue) {
    }

    public record AttendanceRangeTime(LocalDateTime startTime, LocalDateTime endTime) {
    }

    public record AttendanceReportValue(String status, double leaveTime, double attendanceTime, double bonusTime, String note) {

    }
}
