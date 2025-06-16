package com.designsink.dsink.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.designsink.dsink.entity.product.ProductItem;

@Repository
public interface ProductItemRepository extends JpaRepository<ProductItem, Integer> {
}
