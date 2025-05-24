package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkinout.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
public class Checkinout implements UseCase {
    private String userId;
    private LocalDateTime pointTime;

    public static Checkinout from(String userId) {
        return Checkinout.builder()
                .userId(userId)
                .pointTime(LocalDateTime.now())
                .build();
    }
}
