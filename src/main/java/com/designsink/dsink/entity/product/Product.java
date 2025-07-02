package com.designsink.dsink.entity.product;

import java.util.List;

import com.designsink.dsink.entity.common.TimeStampEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "product")
@Entity(name = "product")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class Product extends TimeStampEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(unique = true, nullable = false, length = 255)
	private String originalPath;

	@Column(unique = true, nullable = false, length = 255)
	private String thumbnailPath;

	@Builder.Default
	private Boolean isDeleted = false;

	private Integer sequence;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductItem> productItems;

	public void delete() {
		this.isDeleted = true;
	}

	public void updateSequence(Integer sequence) {
		this.sequence = sequence;
	}
}
