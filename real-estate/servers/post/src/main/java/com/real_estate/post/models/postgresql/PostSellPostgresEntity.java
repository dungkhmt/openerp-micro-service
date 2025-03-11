package com.real_estate.post.models.postgresql;

//import com.vladmihalcea.hibernate.type.array.ListArrayType;

import com.real_estate.post.utils.PostStatus;
import com.real_estate.post.utils.TypeDirection;
import com.real_estate.post.utils.TypeLegalDocument;
import com.real_estate.post.utils.TypeProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(
	name = "post_sell"
)
public class PostSellPostgresEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "post_sell_id")
	Long postSellId;

	@Column(name = "author_id")
	Long authorId;

	@Column(name = "title")
	String title;

	@Column(name = "description", columnDefinition="TEXT")
	String description;

	@Enumerated(EnumType.STRING)
	@Column(name = "type_property")
	TypeProperty typeProperty;

	@Column(name = "price", nullable = false)
	Long price;

	@Column(name = "price_per_m2")
	Float pricePerM2;

	@Column(name = "acreage", nullable = false)
	Long acreage;

	@Column(name = "bathroom")
	Integer bathroom;

	@Column(name = "parking")
	Integer parking;

	@Column(name = "bedroom")
	Integer bedroom;

	@Column(name = "floor", nullable = false)
	Integer floor;


	@Column(name = "position")
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "post_sell_postgres_entity_position", joinColumns = @JoinColumn(name = "post_sell_id"))
	List<Float> position;

	@Column(name = "province_id")
	String provinceId;

	@Column(name = "name_province")
	String nameProvince;

	@Column(name = "district_id")
	String districtId;

	@Column(name = "name_district")
	String nameDistrict;

	@Column(name = "address")
	String address;

	@Column(name = "legal_document")
	@Enumerated(EnumType.STRING)
	TypeLegalDocument legalDocument;

	@Column(name = "direction_property")
	@Enumerated(EnumType.STRING)
	TypeDirection directionProperty;

	@Column(name = "horizontal")
	Float horizontal;

	@Column(name = "vertical")
	Float vertical;

	@Column(name = "image_urls")
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "post_sell_postgres_entity_image_urls", joinColumns = @JoinColumn(name = "post_sell_id"))
	List<String> imageUrls;

	@Column(name = "post_status", columnDefinition = "varchar(255) default 'OPENING'")
	@Enumerated(EnumType.STRING)
	PostStatus postStatus;

	@Column(name = "is_available")
	@ColumnDefault(value = "false")
	Boolean isAvailable;

	@Column(name = "created_at")
	Long createdAt;

	@Column(name = "updated_at")
	Long updatedAt;
}
