package openerp.openerpresourceserver.domain.common.usecase;

import jakarta.annotation.PostConstruct;
import openerp.openerpresourceserver.domain.common.model.UseCase;

public abstract class ObservableUseCasePublisher extends BeanAwareUseCasePublisher {

    @PostConstruct
    public abstract void init();

    public <R, T extends UseCase> void register(Class<T> useCaseClass, UseCaseHandler<R, T> useCaseHandler) {
        UseCaseHandlerRegistry.INSTANCE.register(useCaseClass, useCaseHandler);
    }

    public <T extends UseCase> void register(Class<T> useCaseClass, VoidUseCaseHandler<T> useCaseHandler) {
        UseCaseHandlerRegistry.INSTANCE.register(useCaseClass, useCaseHandler);
    }

    public <R> void register(Class<R> returnClass, NoUseCaseHandler<R> useCaseHandler) {
        UseCaseHandlerRegistry.INSTANCE.register(returnClass, useCaseHandler);
    }
}
