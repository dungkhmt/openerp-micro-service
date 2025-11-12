package com.real_estate.common.utils;

public enum DirectionsStatus {
	NORTH("NORTH"),
	SOUTH("SOUTH"),
	WEST("WEST"),
	EAST("EAST"),
	EAST_NORTH("EAST_NORTH"),
	EAST_SOUTH("EAST_SOUTH"),
	WEST_SOUTH("WEST_SOUTH"),
	WEST_NORTH("WEST_NORTH");

	private final String text;

	DirectionsStatus(final String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}
