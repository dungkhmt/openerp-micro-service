package openerp.openerpresourceserver.generaltimetabling.model.entity.general;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openerp.openerpresourceserver.generaltimetabling.exception.InvalidFieldException;
import openerp.openerpresourceserver.generaltimetabling.helper.LearningWeekValidator;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "timetabling_general_classes")
public class GeneralClass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
    private Long parentClassId;
    private Integer duration;
    private String groupName;
    @OneToMany(mappedBy = "generalClass", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<RoomReservation> timeSlots = new ArrayList<RoomReservation>();
    private String learningWeeks;
    private String foreignLecturer;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeneralClass that = (GeneralClass) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moduleName, moduleCode, classCode);
    }

    public void addTimeSlot(RoomReservation roomReservation) {
        timeSlots.add(roomReservation);
    }

    @Override
    public String toString() {
        return refClassId +"/"+ parentClassId +"/"+ classCode + " " + moduleCode + " " + moduleName + " " + timeSlots.toString();
    }

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

    public List<Integer> extractLearningWeeks(){
        String[] terms = learningWeeks.split(",");
        List<Integer> W = new ArrayList();
        try {
            if (terms != null) {
                for (String t : terms) {
                    if (!t.contains("-")) {
                        int w = Integer.valueOf(t);
                    }else{
                        String[] s = t.split("-");
                        if(s != null && s.length == 2){
                            int start = Integer.valueOf(s[0]);
                            int end = Integer.valueOf(s[1]);
                            for(int w = start; w <= end; w++) W.add(w);
                        }else{
                            return new ArrayList<>();// not correct format
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>(); // not correct format
        }
        return W;
    }

    public boolean hasNonNullTimeSlot() {
        return timeSlots.stream().anyMatch(RoomReservation::isTimeSlotNotNull);
    }

    public void setInfo(GeneralClass generalClass) {
        this.moduleCode = generalClass.moduleCode;
        this.moduleName = generalClass.moduleName;
          this.quantityMax = generalClass.quantityMax;
         this.classType = generalClass.classType;
        this.studyClass = generalClass.studyClass;
         this.mass = generalClass.mass;
         this.crew = generalClass.crew;
         this.openBatch = generalClass.openBatch;
         this.course = generalClass.course;
    }
}
