package com.real_estate.post.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationData {
	private int exitcode;

	@JsonProperty("data")
	private DataPage dataPage;

	@JsonProperty("message")
	private String message;
}

