package com.designsink.dsink.repository.product;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.designsink.dsink.entity.product.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
	@Query("""
        SELECT DISTINCT p
          FROM product p
          JOIN p.productItems pi
         WHERE p.isDeleted = false
           AND pi.category <> com.designsink.dsink.entity.product.enums.ProductType.MAIN
    """)
	Slice<Product> findActiveProductsExcludingMainCategory(Pageable pageable);
}
