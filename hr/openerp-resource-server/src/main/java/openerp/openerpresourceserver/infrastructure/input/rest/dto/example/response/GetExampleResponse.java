package openerp.openerpresourceserver.infrastructure.input.rest.dto.example.response;

import lombok.*;
import openerp.openerpresourceserver.domain.model.ExampleModel;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class GetExampleResponse {
    public static GetExampleResponse fromModel(ExampleModel model) {
        return GetExampleResponse.builder()
                .build();
    }
}
