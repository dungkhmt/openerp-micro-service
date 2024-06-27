package com.example.api.controllers.admin.dto;

import com.example.api.services.account.dto.ParentSearchInput;
import com.example.shared.utils.PageableUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParentFilterParam {
    private Long id;

    private String name;

    private String role;

    private String searchBy;

    private String phoneNumber;

    private Long studentId;

    private int page;

    private int size;

    private String sortBy;

    public ParentSearchInput toInput() {
        return ParentSearchInput.builder()
            .id(this.id)
            .name(this.name)
            .role(this.role)
            .phoneNumber(this.phoneNumber)
            .studentId(this.studentId)
            .searchBy(ParentSearchInput.SearchBy.fromValue(this.searchBy))
            .pageable(PageableUtils.generate(this.page, this.size, sortBy, "-createdAt"))
            .build();
    }
}
