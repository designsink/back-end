package com.designsink.dsink.service.product.dto.response;

import java.util.List;

import com.designsink.dsink.entity.product.enums.ProductType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailResponseDto {
	private String path;
	private List<ProductType> categories;
}
