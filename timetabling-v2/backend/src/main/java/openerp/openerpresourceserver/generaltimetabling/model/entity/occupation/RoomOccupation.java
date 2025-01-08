package openerp.openerpresourceserver.generaltimetabling.model.entity.occupation;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "timetabling_room_occupations")
public class RoomOccupation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String classRoom;
    private String classCode;

    private String semester;

    private Integer startPeriod;
    private Integer endPeriod;
    private String crew;
    private Integer dayIndex;
    private Integer weekIndex;

    private String status;

    public RoomOccupation( String classRoom,  String classCode, Integer startPeriod, Integer endPeriod,
            String crew, Integer dayIndex, Integer weekIndex, String status, String semester) {
        this.classRoom = classRoom;
        this.classCode = classCode;
        this.startPeriod = startPeriod;
        this.endPeriod = endPeriod;
        this.crew = crew;
        this.dayIndex = dayIndex;
        this.weekIndex = weekIndex;
        this.status = status;
        this.semester = semester;
    }

    @Override
    public String toString() {
        return classCode + " " + classRoom + " " + dayIndex + " " + weekIndex + " " + startPeriod + "/" + endPeriod + " " + semester;
    }
}
