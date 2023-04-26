package com.hust.baseweb.model;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;

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
