package com.hust.openerp.taskmanagement.multitenancy.util;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class OrganizationContext {
    private static final InheritableThreadLocal<String> CURRENT_ORGANIZATION_CODE =
        new InheritableThreadLocal<>();

    public static String getCurrentOrganizationCode() {
        return CURRENT_ORGANIZATION_CODE.get();
    }

    public static void setCurrentOrganizationCode(String organizationCode) {
        CURRENT_ORGANIZATION_CODE.set(organizationCode);
    }

    public static void clear() {
        CURRENT_ORGANIZATION_CODE.remove();
    }
}
