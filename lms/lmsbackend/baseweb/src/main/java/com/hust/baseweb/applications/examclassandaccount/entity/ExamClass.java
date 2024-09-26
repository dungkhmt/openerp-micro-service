package com.hust.baseweb.applications.examclassandaccount.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "exam_class")
public class ExamClass {
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_DISABLE = "DISABLED";

    public static List<String> getStatusList(){
        List<String> res= new ArrayList<String>();
        res.add(STATUS_ACTIVE);
        res.add(STATUS_DISABLE);
        return res;
    }
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID Id;

    @Column(name="name")
    private String name;

    @Column(name="description")
    private String description;

    @Column(name="status")
    private String status;

    @Column(name="execute_date")
    private String executeDate;

    @Column(name="created_by_user_id")
    private String createdByUserId;


}
