package com.hust.openerp.taskmanagement.hr_management.domain.common.usecase;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PageWrapper;
import java.util.Collection;


public interface PageWrapperUseCaseHandler<R, T extends UseCase> {
    PageWrapper<R> handle(T useCase);
}
