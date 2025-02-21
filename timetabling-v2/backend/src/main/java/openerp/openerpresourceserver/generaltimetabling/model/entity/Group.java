package openerp.openerpresourceserver.generaltimetabling.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "timetabling_group")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id", updatable = false, nullable = false)
    private Long id;

    @Column(name="group_name")
    private String groupName;

    @Column(name="priority_building")
    private String priorityBuilding;

    @Column(name="slot_seq")
    private String slotSeq;

    @Column(name="day_seq")
    private String daySeq;
}
