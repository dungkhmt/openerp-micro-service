package openerp.openerpresourceserver.infrastructure.input.rest.dto.checkinout.response;

import lombok.*;
import openerp.openerpresourceserver.constant.CheckinoutType;
import openerp.openerpresourceserver.domain.model.CheckinoutModel;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
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
