package com.hust.openerp.taskmanagement.dto.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMeetingStatusForm {
	@NotBlank
	String statusId;
}
