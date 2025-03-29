package com.hust.openerp.taskmanagement.dto;

import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MeetingAutoAssignResponseDTO {
	private Map<String, UUID> assignment;
    private int minimalMaxLoad;
}
