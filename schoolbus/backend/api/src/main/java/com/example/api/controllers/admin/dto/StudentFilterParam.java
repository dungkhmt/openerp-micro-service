package com.example.api.controllers.admin.dto;

import com.example.api.services.account.dto.StudentSearchInput;
import com.example.shared.utils.PageableUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentFilterParam {
    private Long id;

    private String name;

    private String studentClass;

    private String phoneNumber;

    private int page;

    private int size;

    private String sortBy;

    public StudentSearchInput toInput() {
        return StudentSearchInput.builder()
            .id(this.id)
            .name(this.name)
            .studentClass(this.studentClass)
            .phoneNumber(this.phoneNumber)
            .pageable(PageableUtils.generate(this.page, this.size, sortBy, "-createdAt"))
            .build();
    }
}
