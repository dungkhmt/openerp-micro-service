package com.hust.baseweb.applications.education.thesisdefensejury.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ThesisFilter {
    private String thesisPlanId;
    private UUID juryId;
    private String key;
}
