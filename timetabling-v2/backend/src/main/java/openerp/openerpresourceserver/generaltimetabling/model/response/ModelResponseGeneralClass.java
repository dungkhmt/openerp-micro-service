package openerp.openerpresourceserver.generaltimetabling.model.response;

import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.GeneralClass;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.RoomReservation;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelResponseGeneralClass {
    private Long id;
    private Integer quantity;
    private Integer quantityMax;
    private String moduleCode;
    private String moduleName;
    private String classType;
    private String classCode;
    private String semester;
    private String studyClass;
    private String mass;
    private String state;
    private String crew;
    private String openBatch;
    private String course;
    private Long refClassId;
    private String programName;
    private Long parentClassId;
    private Integer duration;
    private String groupName;
    private List<RoomReservation> timeSlots = new ArrayList<RoomReservation>();
    private String learningWeeks;
    private String foreignLecturer;
    private List<ModelResponseGeneralClass> subClasses;

    public ModelResponseGeneralClass(GeneralClass gc){
        this.id = gc.getId();
        this.classType = gc.getClassType();
        this.crew = gc.getCrew();
        this.learningWeeks = gc.getLearningWeeks();
        this.mass = gc.getMass();
        this.classCode = gc.getClassCode();
        this.course = gc.getCourse();
        this.duration = gc.getDuration();
        this.foreignLecturer = gc.getForeignLecturer();
        this.groupName = gc.getGroupName();
        this.moduleCode = gc.getModuleCode();
        this.parentClassId = gc.getParentClassId();
        this.quantityMax = gc.getQuantityMax();
        this.refClassId = gc.getRefClassId();
        this.semester = gc.getSemester();
    }
}
