package openerp.openerpresourceserver.dto.request.employee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeRequest {
    private Long id;

    @NotBlank(message = "Employee ID must not be null")
    private String employeeId;

    @NotBlank(message = "User login ID must not be null")
    private String userLoginId;

    @NotNull(message = "Organization Id must not be null")
    private Long organizationId;

    @NotNull(message = "Attendance Range ID must not be null")
    private Long attendanceRangeId;

    @NotNull(message = "Position ID must not be null")
    private Long positionId;

    @NotBlank(message = "Email must not be null")
    private String email;

    @NotBlank(message = "Full name must not be null")
    private String fullName;

    @Pattern(regexp = "^$|\\d{10,11}", message = "Phone number must be 10 to 11 digits")
    private String phone;

    @NotNull(message = "Annual leave must not be null")
    private Double annualLeave;

    private String profileUrl;

    private String title;

    private Boolean isLead;

    private Integer status;
}
