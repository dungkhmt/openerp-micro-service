package com.real_estate.post.models.postgresql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(
	name = "post_buy"
)
public class PostBuyPostgresEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "post_buy_id")
	Long postBuyId;

	@Column(name = "author_id")
	Long authorId;

	@Column(name = "title")
	String title;

	@Column(name = "description")
	String description;

	@Column(name = "type_property")
	String typeProperty;

	@Column(name = "min_acreage")
	Long minAcreage;

	@Column(name = "max_acreage")
	Long maxAcreage;

	@Column(name = "min_price")
	Long minPrice;

	@Column(name = "max_price")
	Long maxPrice;

	@Column(name = "min_bathroom")
	Integer minBathroom;

	@Column(name = "main_parking")
	Integer minParking;

	@Column(name = "min_bedroom")
	Integer minBedroom;

	@Column(name = "min_floor")
	Integer minFloor;

	@Column(name = "legal_documents")
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "post_buy_postgres_entity_legal_documents", joinColumns = @JoinColumn(name = "post_buy_id"))
	List<String> legalDocuments;

	@Column(name = "direction_properties")
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "post_buy_postgres_entity_direction_properties", joinColumns = @JoinColumn(name = "post_buy_id"))
	List<String> directionProperties;

	@Column(name = "min_horizontal")
	Long minHorizontal;

	@Column(name = "min_vertical")
	Long minVertical;

	@Column(name = "provinceId")
	String provinceId;

	@Column(name = "name_pronvince")
	String nameProvince;

	@Column(name = "districtIds")
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "post_buy_postgres_entity_district_ids", joinColumns = @JoinColumn(name = "post_buy_id"))
	List<String> districtIds;

	@Column(name = "name_districts")
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "post_buy_postgres_entity_name_districts", joinColumns = @JoinColumn(name = "post_buy_id"))
	List<String> nameDistricts;

	@Column(name = "post_status")
	String postStatus;

	@Column(name = "is_available")
	Boolean isAvailable;

	@Column(name = "created_at")
	Long createdAt;

	@Column(name = "updated_at")
	Long updatedAt;

}
