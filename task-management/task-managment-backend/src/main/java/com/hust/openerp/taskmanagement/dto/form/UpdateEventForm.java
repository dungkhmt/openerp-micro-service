package com.hust.openerp.taskmanagement.dto.form;

import java.util.Date;
import java.util.List;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventForm {
	@Nullable
    private String name;
	@Nullable
    private Date dueDate;
	@Nullable
    private String description;
	@Nullable
	private List<String> userIds;
}
