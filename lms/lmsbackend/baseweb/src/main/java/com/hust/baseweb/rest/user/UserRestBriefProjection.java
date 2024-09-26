package com.hust.baseweb.rest.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;

/**
 * UserRestBriefProjection
 */
@Projection(name = "brief", types = {DPerson.class})
public interface UserRestBriefProjection {

    String getPartyCode();

    @Value("#{target.person.firstName} #{target.person.lastName}")
    String getFullName();

    @Value("#{target.userLogin.userLoginId}")
    String getUserLoginId();

    @Value("#{target.createdDate}")
    Date getCreatedDate();

    @Value("#{target.type.partyTypeId}")
    String getPartyType();

    //  @Value("#{target.status.description}")
    String getStatus();

    String getPartyId();

    String enabled();
}
