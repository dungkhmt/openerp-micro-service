package com.hust.openerp.taskmanagement.dto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class MeetingAutoAssignRequestDTO {
	@NotNull
	private Map<String, List<UUID>> memberPreferences;
}
