package openerp.openerpresourceserver.firstyeartimetabling.entity.occupation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "first_year_timetabling_room_occupations")
public class FirstYearRoomOccupation {
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

    public FirstYearRoomOccupation(String classRoom, String classCode, Integer startPeriod, Integer endPeriod,
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
