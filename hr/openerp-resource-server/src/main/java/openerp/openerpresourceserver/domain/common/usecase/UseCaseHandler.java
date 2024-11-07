package openerp.openerpresourceserver.domain.common.usecase;

import openerp.openerpresourceserver.domain.common.model.UseCase;

public interface UseCaseHandler<R, T extends UseCase> {

    R handle(T useCase);
}
