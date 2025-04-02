package com.hust.openerp.taskmanagement.dto.form;

import java.util.List;

import com.hust.openerp.taskmanagement.dto.MeetingPlanUserDTO;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class UpdateMemberAssignmentsForm {
	@NotNull
	private List<MeetingPlanUserDTO> assignments;
}
