package com.hust.baseweb.applications.programmingcontest.model;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class LibraryResponseDTO {
    private UUID id;
    private String name;
    private String language;
    private String content;
    private String status;
    private Date lastUpdatedStamp;

    public LibraryResponseDTO(UUID id, String name, String language, String content, String status, Date lastUpdatedStamp) {
        this.id = id;
        this.name = name;
        this.language = language;
        this.content = content;
        this.status = status;
        this.lastUpdatedStamp = lastUpdatedStamp;
    }
}
