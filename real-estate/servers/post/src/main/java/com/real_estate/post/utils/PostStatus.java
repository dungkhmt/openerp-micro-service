package com.real_estate.post.utils;

public enum PostStatus {
	CLOSED("CLOSED"),
	OPENING("OPENING"),
	DONE("DONE");

	private final String text;

	PostStatus(final String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}

}
