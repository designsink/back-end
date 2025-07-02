package com.designsink.dsink.service.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductsSliceResponseDto {
	private Integer productId;
	private Integer sequence;
	private String path;
}
