package com.designsink.dsink.service.product;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.designsink.dsink.entity.product.Product;
import com.designsink.dsink.entity.product.ProductItem;
import com.designsink.dsink.entity.product.enums.ProductType;
import com.designsink.dsink.exception.CustomException;
import com.designsink.dsink.exception.ErrorCode;
import com.designsink.dsink.repository.product.ProductItemRepository;
import com.designsink.dsink.repository.product.ProductRepository;
import com.designsink.dsink.service.product.dto.request.ProductModifyRequestDto;
import com.designsink.dsink.service.product.dto.request.ProductSaveRequestDto;
import com.designsink.dsink.service.product.dto.response.ProductDetailResponseDto;
import com.designsink.dsink.service.product.dto.response.ProductsResponseDto;
import com.designsink.dsink.service.product.dto.response.ProductsSliceResponseDto;
import com.designsink.dsink.service.product.record.ImagePath;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ProductService {

	private final FileStorageService storageService;
	private final ProductRepository productRepository;
	private final ProductItemRepository productItemRepository;

	@Transactional
	public void save(ProductSaveRequestDto requestDto) {
		List<MultipartFile> files = requestDto.getFile();
		List<String> categories = requestDto.getCategories();

		if (files.isEmpty()) {
			throw new CustomException(ErrorCode.FILE_EMPTY);
		}

		// 유효한 enum 검사
		Set<String> productTypeNames = Arrays.stream(ProductType.values())
			.map(Enum::name)
			.collect(Collectors.toSet());

		List<String> invalidNames = categories.stream()
			.filter(name -> !productTypeNames.contains(name))
			.toList();

		if (!invalidNames.isEmpty()) {
			throw new CustomException(ErrorCode.FILE_CATEGORY_ERROR);
		}

		// 실제 enum으로 변환
		List<ProductType> validCategories = categories.stream()
			.map(ProductType::valueOf)
			.distinct()
			.toList();

		// 1. 각 카테고리별 가장 큰 sequence 구하기
		Integer maxSequence = productItemRepository.findMaxSequenceByCategories(validCategories)
			.orElse(0); // 없으면 기본값 0

		int nextSequence = maxSequence + 1;

		// 2. 파일 저장 & Product + ProductItem 저장
		for (MultipartFile file : files) {
			ImagePath paths = storageService.store(file);

			Product newProduct = Product.builder()
				.originalPath(paths.originalPath())
				.thumbnailPath(paths.thumbnailPath())
				.sequence(nextSequence++)
				.build();

			productRepository.save(newProduct);

			for (ProductType category : validCategories) {
				ProductItem productItem = ProductItem.builder()
					.product(newProduct)
					.category(category)
					.build();
				productItemRepository.save(productItem);
			}
		}
	}

	@Transactional
	public void delete(Integer productId) {
		Product findProduct = productRepository.findById(productId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

		storageService.delete(findProduct.getOriginalPath());
		storageService.delete(findProduct.getThumbnailPath());
		findProduct.delete();
	}

	public ProductsResponseDto findAll(ProductType category, int page, int size) {
		// ➊ Sort 설정
		Sort sort;
		if (category == null) {
			sort = Sort.by("createdAt").descending();
		} else {
			sort = Sort.by("sequence").descending();
		}

		// ➋ PageRequest 직접 생성
		Pageable pageable = PageRequest.of(page, size, sort);

		if (category == null) {
			Slice<Product> slice = productRepository
				.findActiveProductsExcludingMainCategory(pageable);

			List<ProductsSliceResponseDto> dtos = slice.getContent().stream()
				.map(p -> ProductsSliceResponseDto.builder()
					.productId(p.getId())
					.sequence(p.getSequence())
					.path(p.getThumbnailPath())
					.build())
				.toList();

			// ➎ 결과 래퍼에 담아 반환
			return ProductsResponseDto.builder()
				.products(dtos)
				.hasNext(slice.hasNext())
				.build();
		}

		Slice<ProductItem> slice = productItemRepository.findAllByCategory(category, pageable);

		List<ProductsSliceResponseDto> dtos = slice.getContent().stream()
			.map(pi -> ProductsSliceResponseDto.builder()
				.productId(pi.getProduct().getId())
				.sequence(pi.getProduct().getSequence())
				.path(pi.getProduct().getThumbnailPath())
				.build())
			.toList();

		return ProductsResponseDto.builder()
			.products(dtos)
			.hasNext(slice.hasNext())
			.build();
	}

	public ProductDetailResponseDto findById(Integer productId) {
		Product findProduct = productRepository.findById(productId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

		if (findProduct.getIsDeleted()) {
			throw new CustomException(ErrorCode.NOT_FOUND_PRODUCT);
		}

		List<ProductType> findProductTypes = productItemRepository.findDistinctCategoriesByProductId(productId);

		return ProductDetailResponseDto.builder()
			.path(findProduct.getOriginalPath())
			.categories(findProductTypes)
			.build();
	}

	public List<Map<String, String>> getCategories() {
		return Arrays.stream(ProductType.values())
			.map(type -> Map.of("label", type.getLabel(), "name", type.name()))
			.toList();
	}

	@Transactional
	public void modifySequences(ProductType productType, List<ProductModifyRequestDto> requestDtos) {
		List<Integer> ids = requestDtos.stream()
			.map(ProductModifyRequestDto::getId)
			.toList();

		// 1. category가 일치하는 ProductItem들만 조회
		List<ProductItem> productItems = productItemRepository.findByProductIdInAndCategory(ids, productType);

		// 2. ID → sequence 매핑
		Map<Integer, Integer> idToSequenceMap = requestDtos.stream()
			.collect(Collectors.toMap(ProductModifyRequestDto::getId, ProductModifyRequestDto::getSequence));

		// 3. productId 기준 중복 제거 (한 product에 여러 item 있을 수 있음)
		Set<Product> matchedProducts = productItems.stream()
			.map(ProductItem::getProduct)
			.collect(Collectors.toSet());

		// 4. sequence 업데이트
		for (Product product : matchedProducts) {
			Integer newSequence = idToSequenceMap.get(product.getId());
			if (newSequence != null) {
				product.updateSequence(newSequence);
			}
		}
	}
}
