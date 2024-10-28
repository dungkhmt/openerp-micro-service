package com.hust.baseweb.applications.education.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter

public class EduCourseChapterMaterialModelCreate {

    private UUID chapterId;
    private String materialName;
    private String materialType;

}
