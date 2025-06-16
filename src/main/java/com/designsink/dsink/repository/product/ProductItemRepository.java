package com.designsink.dsink.repository.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.designsink.dsink.entity.product.ProductItem;
import com.designsink.dsink.entity.product.enums.ProductType;

@Repository
public interface ProductItemRepository extends JpaRepository<ProductItem, Integer> {
	void deleteAllByProductId(Integer productId);

	List<ProductItem> findAllByCategory(ProductType category);
}
