package openerp.openerpresourceserver.application.port.out.example.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.domain.common.model.UseCase;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.request.CheckinoutRequest;

import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
public class ExampleUseCase implements UseCase {
    public static ExampleUseCase from() {
        return ExampleUseCase.builder()
                .build();

    }
}
