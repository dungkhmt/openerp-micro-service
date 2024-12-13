package openerp.openerpresourceserver.labtimetabling.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.util.*;

@Data
@Entity
@Table(name = "timetable_lab_room")
@NoArgsConstructor
public class Room implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Room(Long id) {
        this.id = id;
    }
    private String name;
    private Integer capacity;

    @OneToMany(mappedBy = "room")
    private List<Assign> assigns;

    @ManyToOne
    @JoinColumn(name = "department_id", insertable=false, updatable=false)
    private Department department;

    @Column(name = "department_id")
    private Integer department_id;

//    @CreatedDate
//    @Column(name = "created_at")
//    private Date createdDate;
//
//    @LastModifiedDate
//    @Column(name = "updated_at")
//    private Date lastModifiedDate;
}
