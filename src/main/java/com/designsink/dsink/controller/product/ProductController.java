package com.designsink.dsink.controller.product;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.designsink.dsink.service.product.ProductService;
import com.designsink.dsink.service.product.dto.ProductSaveRequestDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	@PostMapping(value = "/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> createProduct(@ModelAttribute ProductSaveRequestDto requestDto) {
		productService.save(requestDto);
		return ResponseEntity.ok().body("상품 등록 성공");
	}

	@GetMapping("/categories")
	public ResponseEntity<List<Map<String, String>>> getCategories() {
		return ResponseEntity.ok().body(productService.getCategories());
	}
}
