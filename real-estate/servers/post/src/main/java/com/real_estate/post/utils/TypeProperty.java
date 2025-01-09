package com.real_estate.post.utils;

public enum TypeProperty {
	LAND("LAND"),
	HOUSE("HOUSE"),
	APARTMENT("APARTMENT");

	private final String text;

	TypeProperty(final String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}
