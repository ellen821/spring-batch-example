package com.assadev.batch.core.contant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IndexType {
	STATIC("static"),
	DYNAMIC("dynamic");

	private final String value;
}
