package com.designsink.dsink.service.product.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductsResponseDto {
	private List<ProductsSliceResponseDto> products;
	private boolean hasNext;
}
