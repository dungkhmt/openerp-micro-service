package com.hust.openerp.taskmanagement.hr_management.domain.common.usecase;

import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;

public interface VoidUseCaseHandler<T extends UseCase> {

    void handle(T useCase);
}
