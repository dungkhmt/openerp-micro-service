package com.real_estate.post.models.postgresql;

import com.real_estate.post.utils.PostStatus;
import com.real_estate.post.utils.TypeDirection;
import com.real_estate.post.utils.TypeLegalDocument;
import com.real_estate.post.utils.TypeProperty;
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
	@Enumerated(EnumType.STRING)
	TypeProperty typeProperty;

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
	@Enumerated(EnumType.STRING)
	List<TypeLegalDocument> legalDocuments;

	@Column(name = "direction_properties")
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "post_buy_postgres_entity_direction_properties", joinColumns = @JoinColumn(name = "post_buy_id"))
	@Enumerated(EnumType.STRING)
	List<TypeDirection> directionProperties;

	@Column(name = "min_horizontal")
	Float minHorizontal;

	@Column(name = "min_vertical")
	Float minVertical;

	@Column(name = "province_id")
	String provinceId;

	@Column(name = "name_province")
	String nameProvince;

	@Column(name = "district_ids")
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "post_buy_postgres_entity_district_ids", joinColumns = @JoinColumn(name = "post_buy_id"))
	List<String> districtIds;

	@Column(name = "name_districts")
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "post_buy_postgres_entity_name_districts", joinColumns = @JoinColumn(name = "post_buy_id"))
	List<String> nameDistricts;

	@Column(name = "post_status")
	@Enumerated(EnumType.STRING)
	PostStatus postStatus;

	@Column(name = "is_available")
	Boolean isAvailable;

	@Column(name = "created_at")
	Long createdAt;

	@Column(name = "updated_at")
	Long updatedAt;

}
