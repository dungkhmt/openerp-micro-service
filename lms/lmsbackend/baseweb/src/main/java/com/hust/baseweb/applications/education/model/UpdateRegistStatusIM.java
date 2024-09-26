package com.hust.baseweb.applications.education.model;

import com.hust.baseweb.applications.education.classmanagement.enumeration.RegistStatus;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
public class UpdateRegistStatusIM extends RegistIM {

    @NotNull
    @Size(min = 1, message = "Được yêu cầu")
    Set<@NotBlank(message = "Được yêu cầu") String> studentIds;

    @NotNull
    private RegistStatus status;
}
