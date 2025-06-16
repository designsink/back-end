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
import org.springframework.web.bind.annotation.RestController;

import com.designsink.dsink.service.product.ProductService;
import com.designsink.dsink.service.product.dto.request.ProductSaveRequestDto;

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
	public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
		return ResponseEntity.ok().body("<UNK> <UNK> <UNK>");
	}

	@GetMapping("/categories")
	public ResponseEntity<List<Map<String, String>>> getCategories() {
		return ResponseEntity.ok().body(productService.getCategories());
	}
}
