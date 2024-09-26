package com.example.api.controllers.admin.dto;

import com.example.api.services.student_assign.dto.UpsertStudentAssignInput;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpsertStudentAssignRequest {
    private List<Item> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private Long studentId;
        private String numberPlate;

        public UpsertStudentAssignInput.Item toInput() {
            return UpsertStudentAssignInput.Item.builder()
                .studentId(studentId)
                .numberPlate(numberPlate)
                .build();
        }
    }

    public UpsertStudentAssignInput toInput() {
        return UpsertStudentAssignInput.builder()
            .items(items.stream().map(Item::toInput).toList())
            .build();
    }
}
