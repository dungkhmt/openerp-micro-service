package openerp.openerpresourceserver.application.port.out.staff.usecase_data;

import lombok.*;
import openerp.openerpresourceserver.domain.common.model.UseCase;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
public class ExampleUseCase implements UseCase {
    public static ExampleUseCase from() {
        return ExampleUseCase.builder()
                .build();

    }
}
