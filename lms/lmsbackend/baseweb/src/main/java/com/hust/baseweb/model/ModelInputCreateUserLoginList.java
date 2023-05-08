package com.hust.baseweb.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ModelInputCreateUserLoginList {
    private List<String> roles;
    private List<String> affiliations;

}
