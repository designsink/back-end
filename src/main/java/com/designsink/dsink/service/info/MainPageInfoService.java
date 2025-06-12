package com.designsink.dsink.service.info;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.designsink.dsink.entity.info.MainPageInfo;
import com.designsink.dsink.exception.CustomException;
import com.designsink.dsink.exception.ErrorCode;
import com.designsink.dsink.repository.info.MainPageInfoRepository;
import com.designsink.dsink.service.info.dto.request.MainPageInfoRequestDto;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class MainPageInfoService {

	private final MainPageInfoRepository mainPageInfoRepository;

	@Transactional
	public void create(MainPageInfoRequestDto requestDto) {

		MainPageInfo newMainPageInfo = MainPageInfo.builder()
			.title(requestDto.getTitle())
			.description(requestDto.getDescription())
			.storeName(requestDto.getStoreName())
			.CEOName(requestDto.getCEOName())
			.businessNumber(requestDto.getBusinessNumber())
			.email(requestDto.getEmail())
			.address(requestDto.getAddress())
			.storePhone(requestDto.getStorePhone())
			.phone(requestDto.getPhone())
			.build();

		mainPageInfoRepository.save(newMainPageInfo);

	}

	@Transactional
	public void update(Integer infoId, MainPageInfoRequestDto requestDto) {
		MainPageInfo findInfo = mainPageInfoRepository.findById(infoId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PAGE_INFO));

		findInfo.updateInfo(requestDto);
	}
}
