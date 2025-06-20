package com.hust.openerp.taskmanagement.multitenancy.async;

import com.hust.openerp.taskmanagement.multitenancy.util.OrganizationContext;
import org.springframework.core.task.TaskDecorator;
import org.springframework.lang.NonNull;

import static com.hust.openerp.taskmanagement.multitenancy.util.OrganizationContext.getCurrentOrganizationCode;

public class OrganizationTaskDecorator implements TaskDecorator {

    @Override
    @NonNull
    public Runnable decorate(@NonNull Runnable runnable) {
        String orgCode = OrganizationContext.getCurrentOrganizationCode();
        return () -> {
            try {
                if (orgCode != null) {
                    OrganizationContext.setCurrentOrganizationCode(orgCode);
                }
                runnable.run();
            } finally {
                OrganizationContext.clear();
            }
        };
    }
}
