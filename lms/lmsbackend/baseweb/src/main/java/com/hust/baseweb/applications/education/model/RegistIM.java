package com.hust.baseweb.applications.education.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
public class RegistIM {

    @NotNull
    UUID classId;
}
