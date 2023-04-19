package com.hust.baseweb.applications.education.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentEduCourseDetailIM {
    private String comment;
    private UUID eduCourseMaterialId;
}
