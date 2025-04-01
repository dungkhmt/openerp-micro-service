package openerp.openerpresourceserver.tarecruitment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ta_class_call")
public class ClassCall {

    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "subject_id")
    private String subjectId;

    @Column(name = "subject_name")
    private String subjectName;

    @Column(name = "class_room")
    private String classRoom;

    @Column(name = "semester")
    private String semester;

    @Column(name = "day")
    private int day;

    @Column(name = "start_period")
    private int startPeriod;

    @Column(name = "end_period")
    private int endPeriod;

    @Column(name = "number_slots")
    private int numberSlots; // so tiet

    @Column(name = "note")
    private String note;
}
