package com.designsink.dsink.repository.product;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.designsink.dsink.entity.product.Product;
import com.designsink.dsink.entity.product.enums.ProductType;

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

	@Query("""
    SELECT p
    FROM product p
    JOIN p.productItems pi
    WHERE pi.category = :category
      AND p.isDeleted = false
    ORDER BY p.sequence DESC
""")
	Slice<Product> findByCategory(@Param("category") ProductType category, Pageable pageable);
}
