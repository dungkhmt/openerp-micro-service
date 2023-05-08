package com.hust.baseweb.applications.education.thesisdefensejury.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    private boolean ok;
    private String err;
    private Object result;

}
