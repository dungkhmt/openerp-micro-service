package com.real_estate.common.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReportEntity {
	Long reportId;
	String category;
	String description;
	Long reporterId;
	Long postId;
	Long authorId;
	String status;
	Long createdAt;
	Long updatedAt;
}
