package openerp.openerpresourceserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDetailDTO {
    private UUID id;
    private String name;
    private String username;
    private String password; // Có thể mã hóa hoặc không gửi mật khẩu
    private String email;
    private String phone;
    private String address;
    private String city;
    private String district;
    private String ward;
    private UUID hubId; // Nếu cần chỉ định Hub
    private String hubName;

}
