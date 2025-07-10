package openerp.openerpresourceserver.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "edu_quiz_test_quiz_question")
public class QuizQuestion implements Serializable {

    @Id
    private Long id;

    private String testId;
    private String questionId;
    private String createdByUserId;
    private String statusId;

    @CreatedDate
    @Column(name = "created_stamp")
    private Date createdDate;

    @LastModifiedDate
    @Column(name = "last_updated_stamp")
    private Date lastModifiedDate;

}
