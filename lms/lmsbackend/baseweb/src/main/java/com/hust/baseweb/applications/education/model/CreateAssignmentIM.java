package com.hust.baseweb.applications.education.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class CreateAssignmentIM {

    @NotNull
    private UUID classId;

    @NotBlank
    private String name;

    @NotNull
    private Date openTime;

    @NotNull
    private Date closeTime;

    private String subject;
}
