package com.hust.openerp.taskmanagement.dto.form;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class UpdateMemberRegistrationsForm {
	@NotNull(message = "Session IDs cannot be null")
	private List<UUID> sessionIds;
}
