package openerp.openerpresourceserver.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "timetabling_class_opened")
public class ClassOpened {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_opened_id", updatable = false, nullable = false)
    private Long id;
    private String semester;
    private String quantity;
    private String classType;
    private String moduleCode;
    private String moduleName;
    private String mass;
    private String quantityMax;
    private String studyClass;
    private String state;
    private String classCode;
    private String crew;
    private String openBatch;
    private String course;
    private String groupName;
    private String startPeriod;
    private String weekday;
    private String classroom;
    private String secondStartPeriod;
    private String secondWeekday;
    private String secondClassroom;
    private Boolean isSeparateClass = false;
}
