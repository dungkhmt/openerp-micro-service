package openerp.openerpresourceserver.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class EmployeeResponse {
    private UUID employeeId;
    private String employeeName;

    public EmployeeResponse(UUID employeeId, String employeeName) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
    }
}
