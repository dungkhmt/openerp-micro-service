package com.hust.openerp.taskmanagement.dto.form;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class BatchCreateSessionForm {
    @NotNull
    private List<CreateMeetingSessionForm> sessions;
}
