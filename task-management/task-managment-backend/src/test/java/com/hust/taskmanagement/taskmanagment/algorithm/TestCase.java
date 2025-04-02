package com.hust.taskmanagement.taskmanagment.algorithm;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.List;

public class TestCase {
    private String name;
    @JsonProperty("userChoices")
    private Map<String, List<String>> userChoicesRaw;

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, List<String>> getUserChoicesRaw() {
        return userChoicesRaw;
    }

    public void setUserChoicesRaw(Map<String, List<String>> userChoicesRaw) {
        this.userChoicesRaw = userChoicesRaw;
    }
}