package com.designsink.dsink.service.info.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MainPageInfoDto {
	private String title;
	private String description;
	private String storeName;
	private String ceoName;
	private String businessNumber;
	private String email;
	private String address;
	private String storePhone;
	private String phone;
}
