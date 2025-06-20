package com.hust.openerp.taskmanagement.dto.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationForm {
    @NotBlank(message = "Code is required")
    @Size(max = 100, message = "Code cannot exceed 100 characters")
    private String code;

    @NotBlank(message = "Name is required")
    @Size(max = 500, message = "Name cannot exceed 500 characters")
    private String name;
}
