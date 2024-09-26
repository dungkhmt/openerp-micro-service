package com.hust.baseweb.applications.programmingcontest.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "tag")
public class TagEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "incrementDomain")
    @GenericGenerator(name = "incrementDomain", strategy = "increment")
    @Column(name = "tag_id")
    private Integer tagId;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "description")
    private String description;

}
