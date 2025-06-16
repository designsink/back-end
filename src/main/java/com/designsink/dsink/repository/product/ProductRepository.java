package com.designsink.dsink.repository.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.designsink.dsink.entity.product.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
	List<Product> findByIsDeletedFalseOrderByCreatedAtDesc();
}
