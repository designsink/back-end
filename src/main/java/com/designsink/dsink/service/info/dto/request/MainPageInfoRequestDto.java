package com.designsink.dsink.service.info.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MainPageInfoRequestDto {
	private String title;
	private String description;
	private String storeName;
	private String CEOName;
	private String businessNumber;
	private String email;
	private String address;
	private String storePhone;
	private String phone;
}
