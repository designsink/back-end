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
import com.designsink.dsink.service.product.dto.request.ProductSaveRequestDto;
import com.designsink.dsink.service.product.dto.response.ProductDetailResponseDto;
import com.designsink.dsink.service.product.dto.response.ProductsResponseDto;

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

		findProduct.delete();
	}

	public List<ProductsResponseDto> findAll(ProductType category) {
		if (category == null) {
			return productRepository.findByIsDeletedFalseOrderByCreatedAtDesc().stream()
				.map(productItem -> ProductsResponseDto.builder()
					.productId(productItem.getId())
					.path(productItem.getPath())
					.build())
				.toList();
		}

		return productItemRepository.findAllByCategoryOrderByCreatedAtDesc(category).stream()
			.map(productItem -> ProductsResponseDto.builder()
				.productId(productItem.getProduct().getId())
				.path(productItem.getProduct().getPath())
				.build())
			.toList();
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
