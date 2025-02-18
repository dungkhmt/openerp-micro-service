package com.hust.baseweb.callexternalapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Project {
    private UUID id;

    private String name;

    private String code;

    private String description;

    private String createdStamp;

    private String lastUpdatedStamp;
}
