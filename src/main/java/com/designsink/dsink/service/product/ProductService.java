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
import com.designsink.dsink.service.product.dto.request.ProductSaveRequestDto;
import com.designsink.dsink.service.product.dto.response.ProductDetailResponseDto;
import com.designsink.dsink.service.product.dto.response.ProductsResponseDto;
import com.designsink.dsink.service.product.dto.response.ProductsSliceResponseDto;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ProductService {

	private final FileStorageService storageService;
	private final ProductRepository productRepository;
	private final ProductItemRepository productItemRepository;
	private final FileStorageService fileStorageService;

	@Transactional
	public void save(ProductSaveRequestDto requestDto) {
		List<MultipartFile> files = requestDto.getFile();
		List<String> categories = requestDto.getCategories();

		if (files.isEmpty()) {
			throw new CustomException(ErrorCode.FILE_EMPTY);
		}

		Set<String> productTypeNames = Arrays.stream(ProductType.values())
			.map(Enum::name)
			.collect(Collectors.toSet());

		List<String> invalidNames = categories.stream()
			.filter(name -> !productTypeNames.contains(name))
			.toList();

		if (!invalidNames.isEmpty()) {
			throw new CustomException(ErrorCode.FILE_CATEGORY_ERROR);
		}

		for (MultipartFile file : files) {
			String storedFilename = storageService.store(file);

			Product newProduct = Product.builder()
				.path(storedFilename)
				.build();
			productRepository.save(newProduct);

			List<ProductType> validCategories = categories.stream()
				.map(ProductType::valueOf)
				.distinct()
				.toList();

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

		fileStorageService.delete(findProduct.getPath());
		findProduct.delete();
	}

	public ProductsResponseDto findAll(ProductType category, int page, int size) {
		// ➊ Sort 설정
		Sort sort;
		if (category == null) {
			sort = Sort.by("createdAt").descending();
		} else {
			sort = Sort.by("createdAt").descending();
		}

		// ➋ PageRequest 직접 생성
		Pageable pageable = PageRequest.of(page, size, sort);

		if (category == null) {
			Slice<Product> slice = productRepository
				.findActiveProductsExcludingMainCategory(pageable);

			List<ProductsSliceResponseDto> dtos = slice.getContent().stream()
				.map(p -> ProductsSliceResponseDto.builder()
					.productId(p.getId())
					.path(p.getPath())
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
				.path(pi.getProduct().getPath())
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
			.path(findProduct.getPath())
			.categories(findProductTypes)
			.build();
	}

	public List<Map<String, String>> getCategories() {
		return Arrays.stream(ProductType.values())
			.map(type -> Map.of("label", type.getLabel(), "name", type.name()))
			.toList();
	}
}
