package com.designsink.dsink.service.product;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.designsink.dsink.entity.product.Product;
import com.designsink.dsink.entity.product.ProductItem;
import com.designsink.dsink.entity.product.enums.ProductType;
import com.designsink.dsink.exception.CustomException;
import com.designsink.dsink.exception.ErrorCode;
import com.designsink.dsink.repository.product.ProductItemRepository;
import com.designsink.dsink.repository.product.ProductRepository;
import com.designsink.dsink.service.product.dto.ProductSaveRequestDto;

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
		MultipartFile file = requestDto.getFile();
		List<String> categories = requestDto.getCategories();

		if (file.isEmpty()) {
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

		String storedFilename = storageService.store(file);
		String downloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
			.path("/uploads/")
			.path(storedFilename)
			.toUriString();

		Product newProduct = Product.builder()
			.path(downloadUri)
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

	public List<Map<String, String>> getCategories() {
		return Arrays.stream(ProductType.values())
			.map(type -> Map.of("label", type.getLabel(), "name", type.name()))
			.toList();
	}
}
