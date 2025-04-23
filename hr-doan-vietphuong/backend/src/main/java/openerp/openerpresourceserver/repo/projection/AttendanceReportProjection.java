package openerp.openerpresourceserver.repo.projection;

import java.time.LocalDateTime;

public interface AttendanceReportProjection {
    Long getId();
    Integer getAttendanceId();
    Long getAttendanceRangeId();
    Integer getDate();
    LocalDateTime getStartTime();
    LocalDateTime getEndTime();
    Double getAttendanceTime();
    LocalDateTime getRawStartTime();
    LocalDateTime getRawEndTime();
    Double getRawAttendanceTime();
    Double getLeaveTime();
    Integer getStatus();
    LocalDateTime getUpdatedAt();
    String getUserEmail();
    Integer getNotificationSubscription();
    String getLarkUserId();
}
