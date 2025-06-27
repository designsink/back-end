package com.designsink.dsink.service.info;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.designsink.dsink.entity.info.MainPageInfo;
import com.designsink.dsink.entity.product.ProductItem;
import com.designsink.dsink.entity.product.enums.ProductType;
import com.designsink.dsink.exception.CustomException;
import com.designsink.dsink.exception.ErrorCode;
import com.designsink.dsink.repository.info.MainPageInfoRepository;
import com.designsink.dsink.repository.product.ProductItemRepository;
import com.designsink.dsink.service.info.dto.common.MainPageInfoDto;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class MainPageInfoService {

	private final MainPageInfoRepository mainPageInfoRepository;
	private final ProductItemRepository productItemRepository;

	@Transactional
	public void create(MainPageInfoDto requestDto) {

		MainPageInfo newMainPageInfo = MainPageInfo.builder()
			.title(requestDto.getTitle())
			.description(requestDto.getDescription())
			.storeName(requestDto.getStoreName())
			.ceoName(requestDto.getCeoName())
			.businessNumber(requestDto.getBusinessNumber())
			.email(requestDto.getEmail())
			.address(requestDto.getAddress())
			.storePhone(requestDto.getStorePhone())
			.phone(requestDto.getPhone())
			.build();

		mainPageInfoRepository.save(newMainPageInfo);

	}

	@Transactional
	public void update(Integer infoId, MainPageInfoDto requestDto) {
		MainPageInfo findInfo = mainPageInfoRepository.findById(infoId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PAGE_INFO));

		findInfo.updateInfo(requestDto);
	}

	public MainPageInfoDto find(Integer infoId) {

		List<ProductItem> findItems = productItemRepository.findLatestByEachCategory();

		Map<ProductType, String> pathMap = findItems.stream()
			.collect(Collectors.toMap(
				ProductItem::getCategory,
				item -> item.getProduct().getOriginalPath()
			));

		return mainPageInfoRepository.findById(infoId)
			.map(info -> MainPageInfoDto.builder()
				.title(info.getTitle())
				.description(info.getDescription())
				.storeName(info.getStoreName())
				.ceoName(info.getCeoName())
				.businessNumber(info.getBusinessNumber())
				.email(info.getEmail())
				.address(info.getAddress())
				.storePhone(info.getStorePhone())
				.phone(info.getPhone())
				.sinkPath(pathMap.get(ProductType.SINK))
				.fridgeCabinetPath(pathMap.get(ProductType.FRIDGE_CABINET))
				.builtInClosetPath(pathMap.get(ProductType.BUILT_IN_CLOSET))
				.customFurniturePath(pathMap.get(ProductType.CUSTOM_FURNITURE))
				.build())
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PAGE_INFO));
	}
}
