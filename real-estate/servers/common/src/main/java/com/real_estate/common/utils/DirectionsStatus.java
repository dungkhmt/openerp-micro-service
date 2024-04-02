package com.real_estate.common.utils;

public enum DirectionsStatus {
	NORTH("Bắc"),
	SOUTH("Nam"),
	WEST("Tây"),
	EAST("Đông"),
	EAST_NORTH("Đông Bắc"),
	EAST_SOUTH("Đông Nam"),
	WEST_SOUTH("Tây Nam"),
	WEST_NORTH("Tây Bắc");

	private final String text;

	DirectionsStatus(final String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}
