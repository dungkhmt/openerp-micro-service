package openerp.openerpresourceserver.domain.common.usecase;
import openerp.openerpresourceserver.domain.common.model.UseCase;

import java.util.Collection;


public interface CollectionUseCaseHandler<R, T extends UseCase> {
    Collection<R> handle(T useCase);
}
