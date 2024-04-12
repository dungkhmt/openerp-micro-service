package com.real_estate.post.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Location {
	@JsonProperty("_id")
	private String id;

	private String name;

	private String type;

	private String slug;

	@JsonProperty("name_with_type")
	private String nameWithType;

	private String path;

	@JsonProperty("path_with_type")
	private String pathWithType;

	private String code;

	@JsonProperty("parent_code")
	private String parentCode;

	@JsonProperty("isDeleted")
	private boolean isDeleted;

	// Các getter và setter
}
