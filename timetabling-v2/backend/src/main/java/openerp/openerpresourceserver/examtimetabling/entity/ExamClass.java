package openerp.openerpresourceserver.examtimetabling.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "exam_timetabling_class")
public class ExamClass {
    @Id
    @Column(name="exam_class_id")
    private String examClassId;

    @Column(name="class_id")
    private String classId;
}
