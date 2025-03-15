package com.hust.openerp.taskmanagement.hr_management.domain.common.usecase;
import openerp.openerpresourceserver.domain.common.model.UseCase;
import openerp.openerpresourceserver.domain.model.PageWrapper;

import java.util.Collection;


public interface PageWrapperUseCaseHandler<R, T extends UseCase> {
    PageWrapper<R> handle(T useCase);
}
