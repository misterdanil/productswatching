package com.bebracore.productswatching.dao;

import java.util.List;

import com.bebracore.productswatching.model.Product;
import com.bebracore.productswatching.service.SortType;

import model.Resource;
import model.Review;
import parser.Type;

public interface CustomProductRepository {
	List<Product> getSmartphones(List<String> brand, Integer limit, Integer start, List<String> ram, String rom,
			Double diagonalFrom, Double diagonalTo, Double fromPrice, Double toPrice, SortType sort, String text);

	List<Resource> findResources(String id);

	List<Review> getProductResourceReviews(String id, Type type);

}
