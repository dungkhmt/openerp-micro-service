package openerp.openerpresourceserver.firstyeartimetabling.entity.firstyear;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "first_year_timetabling_first_year_classes")
public class FirstYearClass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String quantity;
    private String quantityMax;
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
    @OneToMany(mappedBy = "firstYearClass", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<FirstYearRoomReservation> timeSlots = new ArrayList<FirstYearRoomReservation>();
    private String learningWeeks;
    private String foreignLecturer;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FirstYearClass that = (FirstYearClass) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moduleName, moduleCode, classCode);
    }

    public void addTimeSlot(FirstYearRoomReservation firstYearRoomReservation) {
        timeSlots.add(firstYearRoomReservation);
    }
    @Override
    public String toString() {
        return refClassId +"/"+ parentClassId +"/"+ classCode + " " + moduleCode + " " + moduleName + " " + timeSlots.toString();
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
}
