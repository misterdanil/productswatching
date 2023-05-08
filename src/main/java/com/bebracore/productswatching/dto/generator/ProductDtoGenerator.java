package com.bebracore.productswatching.dto.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.bebracore.productswatching.dto.ProductDto;
import com.bebracore.productswatching.model.Product;

import model.Resource;

public class ProductDtoGenerator {
	private ProductDtoGenerator() {
	}

	public static ProductDto createDto(Product product) {
		ProductDto dto = new ProductDto();

		dto.setId(product.getId());
		dto.setTitle(product.getTitle());

		double min = product.getResources().get(0).getPrice();
		double max = product.getResources().get(0).getPrice();

		List<Resource> resources = product.getResources();
		for (int i = 0; i < resources.size(); i++) {
			if (resources.get(i).getPrice() < min) {
				min = resources.get(i).getPrice();
			}
			if (resources.get(i).getPrice() > max) {
				max = resources.get(i).getPrice();
			}
		}

		if (product.getRatings() != null && !product.getRatings().isEmpty()) {
			double sum = 0;

			for (Map.Entry<String, Integer> e : product.getRatings().entrySet()) {
				sum += e.getValue();
			}

			double average = sum / product.getRatings().size();
			
			dto.setAverageRating(average);
		}
		else {
			dto.setAverageRating(0.0);
		}
		dto.setFromPrice(min);
		dto.setToPrice(max);
		dto.setCharacteristics(product.getCharacteristics());

		return dto;
	}

	public static List<ProductDto> createDtos(List<Product> products) {
		List<ProductDto> dtos = new ArrayList<>();
		products.forEach(product -> dtos.add(createDto(product)));

		return dtos;
	}
}
