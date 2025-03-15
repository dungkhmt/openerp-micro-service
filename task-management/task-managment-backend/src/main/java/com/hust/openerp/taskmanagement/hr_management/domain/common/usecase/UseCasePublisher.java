package com.hust.openerp.taskmanagement.hr_management.domain.common.usecase;

import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PageWrapper;

import java.util.Collection;

public interface UseCasePublisher {

    <R, T extends UseCase> R publish(Class<R> returnClass, T useCase);

    <R, T extends UseCase> void publish(T useCase);

    @SuppressWarnings("unchecked")
    <R, T extends UseCase> PageWrapper<R> publishPageWrapper(Class<R> returnClass, T useCase);

    <R> R publish(Class<R> returnClass);
    <R, T extends UseCase> Collection<R> publishCollection(Class<R> returnClass, T useCase);
}
