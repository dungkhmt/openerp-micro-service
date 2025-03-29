package com.hust.openerp.taskmanagement.dto.form;

import java.util.Date;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class CreateMeetingSessionForm {
    @NotNull
    private Date startTime;

    @NotNull
    private Date endTime;
}
