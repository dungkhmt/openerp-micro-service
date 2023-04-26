package com.hust.baseweb.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="registered_affiliation")
public class RegisteredAffiliation {
    @Id
    @Column(name="affiliation_id")
    private String affiliationId;

    @Column(name="affiliation_name")
    private String affiliationName;

}
