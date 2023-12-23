package com.hust.baseweb.applications.education.entity;

import java.io.Serializable;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EduClassMaterialId implements Serializable {

    private UUID classId;

    private UUID chapterId;

    private UUID materialId;
}
