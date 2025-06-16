package com.designsink.dsink.entity.product.enums;

import lombok.Getter;

@Getter
public enum ProductType {

	SINK("싱크대"),
	FRIDGE_CABINET("냉장고장"),
	BUILT_IN_CLOSET("붙박이장"),
	CUSTOM_FURNITURE("맞춤가구");

	private final String label;

	ProductType(String label) {
		this.label = label;
	}
}
