package openerp.openerpresourceserver.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "timetabling_schedule")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id", updatable = false, nullable = false)
    private Long id;
    private String semester;
    private String institute;
    private String classCode;
    private String bundleClassCode;
    private String moduleCode;
    private String moduleName;
    private String moduleNameByEnglish;
    private String mass;
    private String notes;
    private String sessionNo;
    private String weekDay;
    private String studyTime;
    private String start;
    private String finish;
    private String crew;
    private String studyWeek;
    private String classRoom;
    private String isNeedExperiment;
    private String numberOfRegistrations;
    private String maxQuantity;
    private String state;
    private String classType;
    private String openBatch;
    private String managementCode;
}
