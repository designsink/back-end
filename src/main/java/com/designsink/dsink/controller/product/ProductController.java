package com.designsink.dsink.controller.product;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.designsink.dsink.entity.product.enums.ProductType;
import com.designsink.dsink.service.product.ProductService;
import com.designsink.dsink.service.product.dto.request.ProductSaveRequestDto;
import com.designsink.dsink.service.product.dto.response.ProductDetailResponseDto;
import com.designsink.dsink.service.product.dto.response.ProductsResponseDto;

import lombok.RequiredArgsConstructor;

@RequestMapping("/products")
@RestController
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> createProduct(@ModelAttribute ProductSaveRequestDto requestDto) {
		productService.save(requestDto);
		return ResponseEntity.ok().body("상품 등록 성공");
	}

	@DeleteMapping("/{productId}")
	public ResponseEntity<String> deleteProduct(@PathVariable Integer productId) {
		productService.delete(productId);
		return ResponseEntity.ok().body("상품 삭제 성공");
	}

	@GetMapping
	public ResponseEntity<ProductsResponseDto> getAllProducts(
		@RequestParam(required = false) ProductType category,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "20") int size
	) {
		return ResponseEntity.ok().body(productService.findAll(category, page, size));
	}

	@GetMapping("/{productId}")
	public ResponseEntity<ProductDetailResponseDto> getProductById(@PathVariable Integer productId) {
		return ResponseEntity.ok().body(productService.findById(productId));
	}

	@GetMapping("/categories")
	public ResponseEntity<List<Map<String, String>>> getCategories() {
		return ResponseEntity.ok().body(productService.getCategories());
	}
}
