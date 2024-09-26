package com.hust.baseweb.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecurityGroupOutputModel {

    private String groupId;
    private String description;

    public SecurityGroupOutputModel() {
        super();

    }

    public SecurityGroupOutputModel(String groupId, String description) {
        super();
        this.groupId = groupId;
        this.description = description;
    }

}
