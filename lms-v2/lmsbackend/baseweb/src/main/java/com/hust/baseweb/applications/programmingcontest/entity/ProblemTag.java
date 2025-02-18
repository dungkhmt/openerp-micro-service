package com.hust.baseweb.applications.programmingcontest.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "problem_tag")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProblemTag {

    @EmbeddedId
    private ProblemTagId id;

}
