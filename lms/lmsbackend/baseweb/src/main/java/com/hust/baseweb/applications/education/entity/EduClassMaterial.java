package com.hust.baseweb.applications.education.entity;

import java.util.UUID;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@IdClass(EduClassMaterialId.class)
@Table(name = "edu_class_material")
@Getter
@Setter
public class EduClassMaterial {
    @Id
    @Column(name = "class_id")
    private UUID classId;

    @Id
    @Column(name = "chapter_id")
    private UUID chapterId;

    @Id
    @Column(name = "material_id")
    private UUID materialId;

    @Column(name = "status")
    private Boolean status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="material_id", nullable=false, insertable = false, updatable = false)
    private EduCourseChapterMaterial eduCourseChapterMaterial;
}
