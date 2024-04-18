package openerp.openerpresourceserver.model.entity.occupation;

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
@Table(name = "room_occupation")
public class RoomOccupation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotNull
    private String classRoom;
    @NotNull
    private String classCode;

    private String semester;

    private int startPeriod;
    private int endPeriod;
    private String crew;
    private int dayIndex;
    private int weekIndex;

    private String status;

    public RoomOccupation(@NotNull String classRoom, @NotNull String classCode, int startPeriod, int endPeriod,
            String crew, int dayIndex, int weekIndex, String status, String semester) {
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

}
