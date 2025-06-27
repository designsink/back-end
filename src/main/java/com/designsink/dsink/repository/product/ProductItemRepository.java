package com.designsink.dsink.repository.product;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.designsink.dsink.entity.product.ProductItem;
import com.designsink.dsink.entity.product.enums.ProductType;

@Repository
public interface ProductItemRepository extends JpaRepository<ProductItem, Integer> {
	void deleteAllByProductId(Integer productId);

	@Query("""
    SELECT pi FROM product_item pi
    JOIN FETCH pi.product p
    WHERE p.isDeleted = false
    AND pi.category = :category
    """)
	Slice<ProductItem> findAllByCategory(@Param("category") ProductType category, Pageable pageable);

	@Query("SELECT DISTINCT pi.category FROM product_item pi WHERE pi.product.id = :productId")
	List<ProductType> findDistinctCategoriesByProductId(@Param("productId") Integer productId);

	@Query("""
    SELECT pi FROM product_item pi
    JOIN FETCH pi.product p
    WHERE p.isDeleted = false
      AND pi.createdAt = (
        SELECT MAX(pi2.createdAt)
        FROM product_item pi2
        JOIN pi2.product p2
        WHERE pi2.category = pi.category
          AND p2.isDeleted = false
    	)
    """)
	List<ProductItem> findLatestByEachCategory();

	@Query("""
    SELECT pi
      FROM product_item pi
      JOIN pi.product p
     WHERE pi.category = :category
       AND p.isDeleted = false
    """)
	List<ProductItem> findAllByCategoryAndProductIsDeletedFalse(@Param("category") ProductType category);
}
