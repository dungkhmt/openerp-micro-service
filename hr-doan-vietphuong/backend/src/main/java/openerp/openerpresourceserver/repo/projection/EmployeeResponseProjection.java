package openerp.openerpresourceserver.repo.projection;

public interface EmployeeResponseProjection {
    Long getId();
    String getEmployeeId();
    String getUserLoginId();
    String getFullName();
    String getEmail();
    String getPhone();
    String getProfileUrl();
    Double getAnnualLeave();
    Integer getStatus();
    Long getPositionId();
    Long getOrganizationId();
    Long getAttendanceRangeId();
    String getJobHistories();
}
