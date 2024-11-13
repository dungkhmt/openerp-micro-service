package com.hust.baseweb.applications.programmingcontest.model;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class ModelCreateLibrary {

    @NotBlank(message = "User ID cannot be blank")
    private String userId;

    @NotBlank(message = "Name cannot be blank")
    @Size(max = 200, message = "Name must be less than 200 characters")
    private String name;

    @NotBlank(message = "Language cannot be blank")
    @Size(max = 200, message = "Language must be less than 200 characters")
    private String language;

    @NotBlank(message = "Content cannot be blank")
    private String content;

    @Size(max = 100, message = "Status must be less than 100 characters")
    private String status;

    @NotNull(message = "Created Stamp cannot be null")
    private Date createdStamp;

    @NotNull(message = "Last Updated Stamp cannot be null")
    private Date lastUpdatedStamp;
}
