package com.example.shared.db.dto;

public interface GetParentAndChildDTO {
    Long getParentId();

    String getParentName();

    String getParentRole();

    String getParentAvatar();

    String getParentDateOfBirth();

    Long getChildId();

    String getChildName();

    String getChildAvatar();

    String getChildDateOfBirth();
}
