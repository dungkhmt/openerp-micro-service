package openerp.openerpresourceserver.application.port.out.checkinout.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.domain.common.model.UseCase;
import openerp.openerpresourceserver.domain.model.CheckinoutModel;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.request.CheckinoutRequest;
import openerp.openerpresourceserver.processor.AutoMapped;

import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
@AutoMapped(target = CheckinoutModel.class)
public class Checkinout implements UseCase {
    private String userId;
    private LocalDateTime pointTime;

    public static Checkinout from(CheckinoutRequest request, String userId) {
        return Checkinout.builder()
                .userId(userId)
                .pointTime(request.getPointTime())
                .build();
    }

    public static Checkinout from(String userId) {
        return Checkinout.builder()
                .userId(userId)
                .pointTime(LocalDateTime.now())
                .build();
    }
}
