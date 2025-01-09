package com.hust.baseweb.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProblemFilter {

    String name;

    String levelIds;

    String tagIds;

    String statusIds;

    int page;

    int size;

}
