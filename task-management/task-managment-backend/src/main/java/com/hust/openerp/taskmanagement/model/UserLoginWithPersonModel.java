package com.hust.openerp.taskmanagement.model;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public interface UserLoginWithPersonModel {

    String getUserLoginId();

    String getEmail();

    String getAffiliations();

    String getFirstName();

    String getMiddleName();

    String getLastName();

    String getGender();

    Date getBirthDate();

    default String getFullName() {
        return StringUtils.joinWith(" ", getFirstName(), getMiddleName(), getLastName());
    }
}
