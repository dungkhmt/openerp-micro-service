package com.hust.baseweb.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class ApproveRegistrationIM {

    @NotBlank(message = "Được yêu cầu")
    private String userLoginId;

    @NotNull(message = "Được yêu cầu")
    @Size(min = 1, message = "Yêu cầu ít nhất một vai trò")
    List<@NotBlank String> roles;
}
