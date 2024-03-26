package openerp.openerpresourceserver.model.entity.general;

import java.util.List;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.model.entity.ClassOpened;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralClassOpened {
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
    private List<RoomReservation> timeSlots;
    private Boolean isSeparateClass = false;


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
}
