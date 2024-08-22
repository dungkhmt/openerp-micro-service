package com.example.shared.db.entities;

import com.example.shared.enumeration.EmployeeRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tieptd_194185_employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account;

    private String name;

    private String avatar;

    private Instant dob;

    @Column(name = "phone_number")
    private String phoneNumber;

    @JoinColumn(name = "bus_id")
    private Long busId;

    @Enumerated(EnumType.STRING)
    private EmployeeRole role;

    @CreatedDate
    @CreationTimestamp
    private Instant createdAt;
    @LastModifiedDate
    @UpdateTimestamp
    private Instant updatedAt;
}
