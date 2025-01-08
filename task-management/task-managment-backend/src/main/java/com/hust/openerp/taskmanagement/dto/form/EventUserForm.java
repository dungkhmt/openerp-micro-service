package com.hust.openerp.taskmanagement.dto.form;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventUserForm {
	@NotNull
	private List<String> userIds;
    @NotNull
    private UUID eventId;
}
