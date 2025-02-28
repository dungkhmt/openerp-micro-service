package openerp.openerpresourceserver.generaltimetabling.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "timetabling_class_group")
@IdClass(ClassGroupId.class)
public class ClassGroup {

    @Id
    @Column(name = "class_id", nullable = false)
    private Long classId;

    @Id
    @Column(name = "group_id", nullable = false)
    private Long groupId;
}
