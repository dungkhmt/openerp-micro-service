package openerp.openerpresourceserver.firstyeartimetabling.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "first_year_timetabling_schedule")
public class FirstYearSchedule {
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
