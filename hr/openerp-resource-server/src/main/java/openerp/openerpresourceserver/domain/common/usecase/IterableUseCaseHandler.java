package openerp.openerpresourceserver.domain.common.usecase;
import openerp.openerpresourceserver.domain.common.model.UseCase;


public interface IterableUseCaseHandler<R, T extends UseCase> {
    Iterable<R> handle(T useCase);
}
