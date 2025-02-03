package com.real_estate.post.models.postgresql;

import com.real_estate.post.utils.TypeProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(
        name = "dashboard"
)
public class DashboardPostgresEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dashboard_id")
    Long dashboardId;

    @Column(name = "district_id")
    String districtId;

    @Column(name = "name_district")
    String nameDistrict;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_property")
    TypeProperty typeProperty;

    @Column(name = "highest_price_per_m2")
    Float highestPricePerM2;

    @Column(name = "lowest_price_per_m2")
    Float lowestPricePerM2;

    @Column(name = "medium_price_per_m2")
    Double mediumPricePerM2;

    @Column(name = "total_post")
    Long totalPost;

    @Column(name = "start_time")
    Long startTime;

    @Column(name = "end_time")
    Long endTime;
}
