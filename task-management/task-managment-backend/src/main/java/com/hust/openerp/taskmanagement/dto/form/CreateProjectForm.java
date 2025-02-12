package com.hust.openerp.taskmanagement.dto.form;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

public class CreateProjectForm {
    @NotBlank
    private String name;
    @NotBlank
    private String code;
    @Nullable
    private String description;

    public CreateProjectForm() {
    }

    public CreateProjectForm(String name, String code, String description) {
        this.name = name;
        this.code = code;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
