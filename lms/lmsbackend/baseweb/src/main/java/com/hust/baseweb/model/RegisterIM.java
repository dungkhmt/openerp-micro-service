package com.hust.baseweb.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class RegisterIM {

    @NotBlank(message = "Được yêu cầu")
    @Size(max = 60, message = "Độ dài vượt quá kích thước cho phép")
    private String userLoginId;

    @NotBlank(message = "Được yêu cầu")
    @Size(min = 11, max = 100, message = "Vui lòng chọn mật khẩu chứa ít nhất 11 kí tự, nhiều nhất 100 kí tự")
    private String password;

    @NotBlank(message = "Được yêu cầu")
    @Size(max = 100, message = "Độ dài vượt quá kích thước cho phép")
    private String firstName;

    @NotBlank(message = "Được yêu cầu")
    @Size(max = 100, message = "Độ dài vượt quá kích thước cho phép")
    private String middleName;

    @NotBlank(message = "Được yêu cầu")
    @Size(max = 100, message = "Độ dài vượt quá kích thước cho phép")
    private String lastName;

    @NotBlank(message = "Được yêu cầu")
    @Size(max = 100, message = "Độ dài vượt quá kích thước cho phép")
    @Pattern(regexp = "^(?=.{1,64}@)[\\p{L}0-9_-]+(\\.[\\p{L}0-9_-]+)*@[^-][\\p{L}0-9-]+(\\.[\\p{L}0-9-]+)*(\\.[\\p{L}]{2,})$",
             message = "Định dạng không hợp lệ")
    private String email;

    @NotNull(message = "Được yêu cầu")
    @Size(min = 1, message = "Yêu cầu ít nhất một vai trò")
    private List<@NotBlank String> roles;

    private List<String> affiliations;
}
