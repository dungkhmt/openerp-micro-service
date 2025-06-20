package com.hust.openerp.taskmanagement.dto.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupUserForm {
    @NotEmpty(message = "User IDs are required")
    private List<String> userIds;
}