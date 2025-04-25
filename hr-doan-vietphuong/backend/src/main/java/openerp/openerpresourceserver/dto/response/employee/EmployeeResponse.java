package openerp.openerpresourceserver.dto.response.employee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.entity.JobHistory;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponse {
    private Long id;
    private String userLoginId;
    private Integer employeeId;
    private String fullName;
    private String email;
    private String phone;
    private String profileUrl;
    private Double annualLeave;
    private Integer status;
    private Long organizationId;
    private Long attendanceRangeId;
    private Long positionId;
    private String jobHistories;
    private List<JobHistory> jobHistoryList;

    public EmployeeResponse(final Long id,
                            final Integer employeeId,
                            final String fullName,
                            final String email,
                            final String phone,
                            final String profileUrl,
                            final Long positionId,
                            final Integer status) {
        this.id = id;
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.profileUrl = profileUrl;
        this.positionId = positionId;
        this.status = status;
    }
}


