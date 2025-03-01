package openerp.openerpresourceserver.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import openerp.openerpresourceserver.entity.enumentity.Role;

import java.util.UUID;

@Data
public class EmployeeRequestDto {
    private UUID id;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String name;
    @NotBlank
    private String phone;
    @NotBlank
    private UUID hub;
    private String email;
    @NotBlank
    private Role role;
    @NotBlank
    private String address;
    @NotBlank
    private String city;
    @NotBlank
    private String district;
    @NotBlank
    private String ward;

}
