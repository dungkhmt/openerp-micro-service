package com.hust.baseweb.applications.education.classmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class EduClassSessionDetailOM {
    private UUID sessionId;
    private String sessionName;
    private UUID classId;
    private Date date;
}
