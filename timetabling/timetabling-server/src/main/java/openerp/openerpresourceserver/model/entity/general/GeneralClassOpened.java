package openerp.openerpresourceserver.model.entity.general;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnTransformer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openerp.openerpresourceserver.helper.RoomReservationConverter;
import openerp.openerpresourceserver.model.entity.ClassOpened;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "timetabling_general_classes")
public class GeneralClassOpened {
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
    private String groupName;
    @OneToMany(mappedBy = "generalClassOpened", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<RoomReservation> timeSlots = new ArrayList<RoomReservation>();
    @Column(name = "is_seperate_class")
    private Boolean isSeparateClass = false;
    private String learningWeeks;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeneralClassOpened that = (GeneralClassOpened) o;

        return Objects.equals(moduleName, that.moduleName) &&
                Objects.equals(moduleCode, that.moduleCode) &&
                Objects.equals(classCode, that.classCode);
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
        return classCode + " " + moduleCode + " " + moduleName + " " + timeSlots.toString();
    }


}
