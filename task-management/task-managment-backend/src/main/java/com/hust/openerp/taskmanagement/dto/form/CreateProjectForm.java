package com.hust.openerp.taskmanagement.dto.form;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProjectForm {
    @NotBlank
    private String name;
    @NotBlank
    private String code;
    @Nullable
    private String description;
}
