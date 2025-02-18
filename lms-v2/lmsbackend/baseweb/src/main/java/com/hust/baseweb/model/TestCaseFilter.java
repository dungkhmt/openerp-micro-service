package com.hust.baseweb.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TestCaseFilter {

    Boolean publicOnly;

    Boolean fullView;

    Integer page;

    Integer size;

}
