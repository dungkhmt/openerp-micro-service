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
public class UpdateProfileForm {
	@Nullable
    private String firstName;
	@Nullable
    private String lastName;
    @Nullable
    private String avatarUrl;
}
