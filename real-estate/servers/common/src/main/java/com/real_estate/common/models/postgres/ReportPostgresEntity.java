package com.real_estate.common.models.postgres;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(
	name = "report"
)
public class ReportPostgresEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "report_id")
	Long reportId;

	@Column(name = "category")
	String category;

	@Column(name = "description")
	String description;

	@Column(name = "reporter_id")
	Long reporterId;

	@Column(name = "post_id")
	Long postId;

	@Column(name = "author_id")
	Long authorId;

	@Column(name = "status")
	String status;

	@Column(name = "created_at")
	Long createdAt;

	@Column(name = "updated_at")
	Long updatedAt;

}
