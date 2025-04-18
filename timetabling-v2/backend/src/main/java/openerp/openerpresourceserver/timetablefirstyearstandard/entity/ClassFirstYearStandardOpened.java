package openerp.openerpresourceserver.timetablefirstyearstandard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "timetabling_class_opened_first_year_standard")
public class ClassFirstYearStandardOpened {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassFirstYearStandardOpened that = (ClassFirstYearStandardOpened) o;

        return Objects.equals(moduleName, that.moduleName) &&
                Objects.equals(moduleCode, that.moduleCode) &&
                Objects.equals(classCode, that.classCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moduleName, moduleCode, classCode);
    }
}
