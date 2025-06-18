package com.hust.openerp.taskmanagement.dto.form;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateEventForm {
	@NotEmpty
    private String name;
    @NotNull
    private Date startDate;
    @Nullable
    private Date dueDate;
	@Nullable
    private String description;
    @NotNull
    private UUID projectId;
    @Nullable
	private List<String> userIds;
}
