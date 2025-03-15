package com.hust.openerp.taskmanagement.hr_management.domain.common.usecase;

import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.model.UseCase;
import openerp.openerpresourceserver.domain.exception.ApiHandlerException;
import openerp.openerpresourceserver.domain.model.PageWrapper;

import java.util.Collection;
import java.util.Objects;

@Slf4j
@DomainComponent
public abstract class BeanAwareUseCasePublisher implements UseCasePublisher {

    @Override
    @SuppressWarnings("unchecked")
    public <R, T extends UseCase> R publish(Class<R> returnClass, T useCase) {
        var useCaseHandler = (UseCaseHandler<R, T>) UseCaseHandlerRegistry.INSTANCE.detectUseCaseHandlerFrom(useCase.getClass());
        validateUseCaseHandlerDetection(useCase, useCaseHandler);
        return useCaseHandler.handle(useCase);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R, T extends UseCase> void publish(T useCase) {
        var voidUseCaseHandler = (VoidUseCaseHandler<T>) UseCaseHandlerRegistry.INSTANCE.detectVoidUseCaseHandlerFrom(useCase.getClass());
        if (Objects.isNull(voidUseCaseHandler)) {
            var useCaseHandler = (UseCaseHandler<R, T>) UseCaseHandlerRegistry.INSTANCE.detectUseCaseHandlerFrom(useCase.getClass());
            validateUseCaseHandlerDetection(useCase, useCaseHandler);
            useCaseHandler.handle(useCase);
        } else {
            validateVoidUseCaseHandlerDetection(useCase, voidUseCaseHandler);
            voidUseCaseHandler.handle(useCase);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R, T extends UseCase> Collection<R> publishCollection(Class<R> returnClass, T useCase) {
        var useCaseHandler = (CollectionUseCaseHandler<R, T>) UseCaseHandlerRegistry.INSTANCE.detectCollectionUseCaseHandlerFrom(useCase.getClass());
        validateCollectionUseCaseHandlerDetection(useCase, useCaseHandler);
        return useCaseHandler.handle(useCase);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R, T extends UseCase> PageWrapper<R> publishPageWrapper(Class<R> returnClass, T useCase) {
        var useCaseHandler = (PageWrapperUseCaseHandler<R, T>)
                UseCaseHandlerRegistry.INSTANCE.detectPageWrapperUseCaseHandlerFrom(useCase.getClass());
        validatePageWrapperUseCaseHandlerDetection(useCase, useCaseHandler);
        return useCaseHandler.handle(useCase);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R publish(Class<R> returnClass) {
        var useCaseHandler = (NoUseCaseHandler<R>)
                UseCaseHandlerRegistry.INSTANCE.detectNoUseCaseHandlerFrom(returnClass);
        validateNoParamUseCaseHandlerDetection(useCaseHandler);
        return useCaseHandler.handle();
    }

    private <R, T extends UseCase> void validateUseCaseHandlerDetection(
            T useCase,
            UseCaseHandler<R, T> useCaseHandler
    ){
        if (Objects.isNull(useCaseHandler)) {
            log.error("Use case handler cannot be detected for the use case: {}, handlers: {}",
                    useCase, UseCaseHandlerRegistry.INSTANCE.getRegistryForUseCaseHandlers());
            throw new ApiHandlerException("api.useCaseHandler.notDetected");
        }
    }

    private <T extends UseCase> void validateVoidUseCaseHandlerDetection(
            T useCase,
            VoidUseCaseHandler<T> useCaseHandler
    )
    {
        if (Objects.isNull(useCaseHandler)) {
            log.error("Void use case handler cannot be detected for the use case: {}, handlers: {}",
                    useCase, UseCaseHandlerRegistry.INSTANCE.getRegistryForVoidUseCaseHandlers());
            throw new ApiHandlerException("api.useCaseHandler.notDetected");
        }
    }

    private <R, T extends UseCase> void validateCollectionUseCaseHandlerDetection
            (T useCase, CollectionUseCaseHandler<R, T> useCaseHandler)
    {
        if (Objects.isNull(useCaseHandler)) {
            log.error("Collection Use case handler cannot be detected for the use case: {}, handlers: {}",
                    useCase, UseCaseHandlerRegistry.INSTANCE.getRegistryForUseCaseHandlers());
            throw new ApiHandlerException("api.useCaseHandler.notDetected");
        }
    }

    private <R, T extends UseCase> void validatePageWrapperUseCaseHandlerDetection(
            T useCase,
            PageWrapperUseCaseHandler<R, T> useCaseHandler
    ) {
        if (Objects.isNull(useCaseHandler)) {
            log.error("PageWrapper Use case handler cannot be detected for the use case: {}, handlers: {}",
                    useCase, UseCaseHandlerRegistry.INSTANCE.getRegistryForUseCaseHandlers());
            throw new ApiHandlerException("api.useCaseHandler.notDetected");
        }
    }

    private <R> void validateNoParamUseCaseHandlerDetection(NoUseCaseHandler<R> useCaseHandler) {
        if (Objects.isNull(useCaseHandler)) {
            log.error("Void use case handler cannot be detected for the handlers: {}",
                    UseCaseHandlerRegistry.INSTANCE.getRegistryForNoUseCaseHandlers());
            throw new ApiHandlerException("api.useCaseHandler.notDetected");
        }
    }
}
