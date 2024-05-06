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
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "post_buy_postgres_entity_type_property", joinColumns = @JoinColumn(name = "post_buy_id"))
	List<String> typeProperty;

	@Column(name = "min_acreage")
	Long minAcreage;

	@Column(name = "from_price")
	Long fromPrice;

	@Column(name = "to_price")
	Long toPrice;

	@Column(name = "from_price_perM2")
	Float fromPricePerM2;

	@Column(name = "to_price_perM2")
	Float toPricePerM2;

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

	@Column(name = "directions_property")
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "post_buy_postgres_entity_directtions_property", joinColumns = @JoinColumn(name = "post_buy_id"))
	List<String> directionsProperty;

	@Column(name = "min_horizontal")
	Long minHorizontal;

	@Column(name = "min_vertical")
	Long minVertical;

	@Column(name = "pronvince")
	String province;

	@Column(name = "district")
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "post_buy_postgres_entity_district", joinColumns = @JoinColumn(name = "post_buy_id"))
	List<String> district;

	@Column(name = "post_status")
	String postStatus;

	@Column(name = "is_available")
	Boolean isAvailable;

	@Column(name = "created_at")
	Long createdAt;

	@Column(name = "updated_at")
	Long updatedAt;

}
