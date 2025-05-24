package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkinout.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.constant.CheckinoutType;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckinoutModel;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetCheckinoutResponse {
    private String userId;
    private LocalDateTime pointTime;
    private CheckinoutType type;

    public static GetCheckinoutResponse fromModel(CheckinoutModel model) {
        return GetCheckinoutResponse.builder()
                .userId(model.getUserId())
                .pointTime(model.getPointTime())
                .type(model.getType())
                .build();
    }
}
