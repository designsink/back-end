package com.designsink.dsink.entity.info;

import com.designsink.dsink.entity.common.TimeStampEntity;
import com.designsink.dsink.service.info.dto.common.MainPageInfoDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "main_page")
@Entity(name = "main_page")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class MainPageInfo extends TimeStampEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 16)
	private String title;

	@Column(length = 64)
	private String description;

	@Column(length = 10)
	private String storeName;

	@Column(length = 10)
	private String ceoName;

	@Column(length = 16)
	private String businessNumber;

	@Column(length = 24)
	private String email;

	@Column(length = 32)
	private String address;

	@Column(length = 13)
	private String storePhone;

	@Column(length = 13)
	private String phone;

	public void updateInfo(MainPageInfoDto requestDto) {
		this.title = requestDto.getTitle();
		this.description = requestDto.getDescription();
		this.storeName = requestDto.getStoreName();
		this.ceoName = requestDto.getCeoName();
		this.businessNumber = requestDto.getBusinessNumber();
		this.email = requestDto.getEmail();
		this.address = requestDto.getAddress();
		this.storePhone = requestDto.getStorePhone();
		this.phone = requestDto.getPhone();
	}
}
