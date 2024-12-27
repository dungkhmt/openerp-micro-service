package openerp.openerpresourceserver.application.port.out.example.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.application.port.out.example.filter.IExampleFilter;
import openerp.openerpresourceserver.domain.common.model.UseCase;
import openerp.openerpresourceserver.domain.model.ExampleModel;

@Data
@Builder
@Getter
@Setter
public class IterableUseCaseExample implements IExampleFilter, UseCase {
    public ExampleModel toModel() {
        return ExampleModel.builder()
                .build();
    }
}
