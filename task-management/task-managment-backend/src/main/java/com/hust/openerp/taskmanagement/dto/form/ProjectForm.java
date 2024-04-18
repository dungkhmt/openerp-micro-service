package com.hust.openerp.taskmanagement.dto.form;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectForm {
    @Nullable
    private String name;
    @Nullable
    private String code;
    @Nullable
    private String description;
}
