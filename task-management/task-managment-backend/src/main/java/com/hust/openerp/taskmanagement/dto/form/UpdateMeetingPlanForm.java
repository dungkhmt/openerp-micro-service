package com.hust.openerp.taskmanagement.dto.form;

import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class UpdateMeetingPlanForm {
    @NotBlank
    private String name;
    
    private String description;

    @NotNull
    private Date registrationDeadline;

    private String location;
}
