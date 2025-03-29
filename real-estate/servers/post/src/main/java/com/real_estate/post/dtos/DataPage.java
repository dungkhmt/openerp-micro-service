package com.real_estate.post.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DataPage {
	@JsonProperty("nItems")
	private int nItems;

	@JsonProperty("nPages")
	private int nPages;

	private Location[] data;
	// Các getter và setter
}
