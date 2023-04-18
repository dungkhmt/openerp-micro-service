package com.hust.baseweb.applications.taskmanagement.dto.form;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class SuggestForm {
    private List<String> skillIds;
    private UUID projectId;
}
