package com.hust.openerp.taskmanagement.hr_management.domain.common.usecase;

import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import jakarta.annotation.PostConstruct;

public abstract class ObservableUseCasePublisher extends BeanAwareUseCasePublisher {

    @PostConstruct
    public abstract void init();

    public <R, T extends UseCase> void register(Class<T> useCaseClass, UseCaseHandler<R, T> useCaseHandler) {
        UseCaseHandlerRegistry.INSTANCE.register(useCaseClass, useCaseHandler);
    }

    public <R, T extends UseCase> void register(Class<T> useCaseClass, CollectionUseCaseHandler<R, T> useCaseHandler) {
        UseCaseHandlerRegistry.INSTANCE.register(useCaseClass, useCaseHandler);
    }

    public <R, T extends UseCase> void register(Class<T> useCaseClass, PageWrapperUseCaseHandler<R, T> useCaseHandler) {
        UseCaseHandlerRegistry.INSTANCE.register(useCaseClass, useCaseHandler);
    }

    public <T extends UseCase> void register(Class<T> useCaseClass, VoidUseCaseHandler<T> useCaseHandler) {
        UseCaseHandlerRegistry.INSTANCE.register(useCaseClass, useCaseHandler);
    }

    public <R> void register(Class<R> returnClass, NoUseCaseHandler<R> useCaseHandler) {
        UseCaseHandlerRegistry.INSTANCE.register(returnClass, useCaseHandler);
    }
}
