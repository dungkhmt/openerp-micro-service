package com.real_estate.post.models.postgresql;

//import com.vladmihalcea.hibernate.type.array.ListArrayType;

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

	@Column(name = "title", nullable = true)
	String title;

	@Column(name = "description", nullable = true)
	String description;

	@Column(name = "type_property")
	String typeProperty;

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

	@Column(name = "province")
	String province;

	@Column(name = "distric")
	String district;

	@Column(name = "address")
	String address;

	@Column(name = "legal_documents")
	String legalDocuments;

	@Column(name = "directions_property")
	String directionsProperty;

	@Column(name = "horizontal")
	Long horizontal;

	@Column(name = "vertical")
	Long vertical;

	@Column(name = "image_urls")
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "post_sell_postgres_entity_image_urls", joinColumns = @JoinColumn(name = "post_sell_id"))
	List<String> imageUrls;

	@Column(name = "post_status", columnDefinition = "varchar(255) default 'OPENING'")
	String postStatus;

	@Column(name = "is_availble")
	@ColumnDefault(value = "false")
	Boolean isAvailable;

	@Column(name = "created_at")
	Long createdAt;

	@Column(name = "updared_at")
	Long updatedAt;
}
