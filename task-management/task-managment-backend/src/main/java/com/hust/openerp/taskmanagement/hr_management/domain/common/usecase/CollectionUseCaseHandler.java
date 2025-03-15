package com.hust.openerp.taskmanagement.hr_management.domain.common.usecase;


import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;

import java.util.Collection;


public interface CollectionUseCaseHandler<R, T extends UseCase> {
    Collection<R> handle(T useCase);
}
