package openerp.openerpresourceserver.domain.common.usecase;

import openerp.openerpresourceserver.domain.common.model.UseCase;

public interface VoidUseCaseHandler<T extends UseCase> {

    void handle(T useCase);
}
