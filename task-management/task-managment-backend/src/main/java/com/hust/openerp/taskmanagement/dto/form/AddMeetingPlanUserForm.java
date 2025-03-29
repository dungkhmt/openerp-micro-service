package com.hust.openerp.taskmanagement.dto.form;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddMeetingPlanUserForm {
    private UUID planId;
    private List<String> userId;
}
