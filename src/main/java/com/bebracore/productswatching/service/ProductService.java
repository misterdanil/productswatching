package com.bebracore.productswatching.service;

import java.util.List;

import com.bebracore.productswatching.model.Product;
import com.bebracore.productswatching.service.error.ProductAlreadyAddedException;
import com.bebracore.productswatching.service.error.ProductNotFoundException;
import com.bebracore.productswatching.service.error.RatedAlreadyException;

import model.Review;
import parser.Type;

public interface ProductService {
	Product findByBrandAndSeriesAndRamAndRom(String brand, String series, String ram, String ramMeasure, String rom,
			String romMeasure);

	Product save(Product product);

	Product getProductById(String id);

	List<Review> getProductResourceReviews(String id, Type type);

	List<Product> getSmartphones(List<String> brands, Integer start, Integer limit, List<String> rams, String rom,
			Double diagonalFrom, Double diagonalTo, Double fromPrice, Double toPrice, SortType sort, String text);

	List<Product> save(List<Product> products);

	void addToFavorite(String productId) throws ProductAlreadyAddedException;

	boolean existsById(String id);

	void rate(String id, Integer rating) throws ProductNotFoundException, RatedAlreadyException;
}
