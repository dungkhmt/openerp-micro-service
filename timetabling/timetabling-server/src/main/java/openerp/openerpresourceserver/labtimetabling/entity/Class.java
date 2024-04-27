package openerp.openerpresourceserver.labtimetabling.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table(name = "timetable_lab_class")
@NoArgsConstructor
public class Class implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Class (Long id){
        this.id = id;
    }

    private Long class_code;
    private Integer quantity;
    private Integer period;
    private String note;
    private String course_name;
    private Long department_id;
    private Long semester_id;

    @OneToMany(mappedBy = "lesson", cascade = {CascadeType.PERSIST})
    private List<Assign> assigns;

    @ManyToOne
    @JoinColumn(name = "semester_id", insertable=false, updatable=false)
    private Semester_ semester;

//    @CreatedDate
//    @Column(name = "created_at")
//    private Date createdDate;
//    //
//    @LastModifiedDate
//    @Column(name = "updated_at")
//    private Date lastModifiedDate;
}
