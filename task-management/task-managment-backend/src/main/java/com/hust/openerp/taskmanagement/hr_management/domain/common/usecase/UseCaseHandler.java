package com.hust.openerp.taskmanagement.hr_management.domain.common.usecase;

import openerp.openerpresourceserver.domain.common.model.UseCase;

public interface UseCaseHandler<R, T extends UseCase> {

    R handle(T useCase);
}
