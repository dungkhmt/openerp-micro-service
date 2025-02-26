package openerp.openerpresourceserver.generaltimetabling.model.entity.general;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.generaltimetabling.exception.InvalidFieldException;
import openerp.openerpresourceserver.generaltimetabling.helper.LearningWeekValidator;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "timetabling_plan_general_classes")
public class PlanGeneralClass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer quantityMax;
    private String classType;
    private String mass;
    private String programName;
    private String moduleCode;
    private String moduleName;
    private String semester;
    private Integer numberOfClasses;
    private Integer lectureMaxQuantity;
    private Integer exerciseMaxQuantity;
    private Integer lectureExerciseMaxQuantity;
    private String learningWeeks;
    private String crew;
    private String weekType;
    private int duration;
    private Date createdStamp;
    private String promotion;
    public void setLearningWeeks(String learningWeeks) {
        if (learningWeeks != null) {
            List<String> weekStringList = List.of(learningWeeks.split(","));
            weekStringList.forEach(weekString -> {
                if (!LearningWeekValidator.isCorrectFormat(weekString))
                    throw new InvalidFieldException("Tuần học không đúng định dạng!");
            });
        }
        this.learningWeeks = learningWeeks;
    }

    public void setLectureMaxQuantity(Integer lectureMaxQuantity) {
        if ((lectureMaxQuantity != null || exerciseMaxQuantity != null) && lectureExerciseMaxQuantity != null) {
            throw new InvalidFieldException("Số lượng tối đa của LT+BT phải trống!");
        } else {
            this.lectureMaxQuantity = lectureMaxQuantity;
        }
    }

    public void setExerciseMaxQuantity(Integer exerciseMaxQuantity) {
        if ((lectureMaxQuantity != null || exerciseMaxQuantity != null) && lectureExerciseMaxQuantity != null) {
            throw new InvalidFieldException("Số lượng tối đa của LT+BT phải trống!");
        } else {
            this.exerciseMaxQuantity = exerciseMaxQuantity;
        }
    }

    public void setLectureExerciseMaxQuantity(Integer lectureExerciseMaxQuantity) {
        if ((lectureMaxQuantity != null || exerciseMaxQuantity != null) && lectureExerciseMaxQuantity != null) {
            throw new InvalidFieldException("Số lượng tối đa của LT và BT cần phải trống!");
        } else {
            this.lectureExerciseMaxQuantity = lectureExerciseMaxQuantity;
        }
    }
}
