package com.designsink.dsink.service.info.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MainProductsResponseDto {
	private Integer productId;
	private String path;
}
