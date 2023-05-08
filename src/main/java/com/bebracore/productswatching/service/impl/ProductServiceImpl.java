package com.bebracore.productswatching.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.bebracore.cabinet.model.User;
import com.bebracore.cabinet.service.UserService;
import com.bebracore.productswatching.dao.ProductRepository;
import com.bebracore.productswatching.model.Product;
import com.bebracore.productswatching.service.ProductService;
import com.bebracore.productswatching.service.SortType;
import com.bebracore.productswatching.service.error.ProductAlreadyAddedException;
import com.bebracore.productswatching.service.error.ProductNotFoundException;
import com.bebracore.productswatching.service.error.RatedAlreadyException;

import model.Review;
import parser.Type;

@Service
public class ProductServiceImpl implements ProductService {
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private UserService userService;

	@Override
	public Product findByBrandAndSeriesAndRamAndRom(String brand, String series, String ram, String ramMeasure,
			String rom, String romMeasure) {
		return productRepository.findByBrandAndSeriesAndRamAndRom(brand, series, ram, ramMeasure, rom, romMeasure);
	}

	@Override
	public List<Product> getSmartphones(List<String> brands, Integer start, Integer limit, List<String> rams, String rom,
			Double diagonalFrom, Double diagonalTo, Double fromPrice, Double toPrice, SortType sort, String text) {
		return productRepository.getSmartphones(brands, start, limit, rams, rom, diagonalFrom, diagonalTo, fromPrice,
				toPrice, sort, text);
	}

	@Override
	public Product save(Product product) {
		return productRepository.save(product);
	}

	@Override
	public Product getProductById(String id) {
		return productRepository.findOneById(id);
	}

	@Override
	public List<Review> getProductResourceReviews(String id, Type type) {
		return productRepository.getProductResourceReviews(id, type);
	}

	@Override
	public List<Product> save(List<Product> products) {
		return productRepository.saveAll(products);
	}

	@Override
	public void addToFavorite(String productId) throws ProductAlreadyAddedException {
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();

		if (userService.existsByFavoriteProduct(userId, productId)) {
			throw new ProductAlreadyAddedException("Такой товар уже добавлен");
		}

		User user = userService.findById(userId);
		user.getFavoriteProducts().add(productId);

		userService.save(user);
	}

	@Override
	public boolean existsById(String id) {
		return productRepository.existsById(id);
	}

	@Override
	public void rate(String id, Integer rating) throws ProductNotFoundException, RatedAlreadyException {
		Product product = productRepository.findOneById(id);
		if (product == null) {
			throw new ProductNotFoundException(
					"Exception occurred while rating product with id: " + id + ". Couldn't find");
		}

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userId = authentication.getName();

		if (product.containsRating(userId)) {
			throw new RatedAlreadyException("Exception occurred while raing product with id: " + id
					+ ". The product has already rated by user " + userId);
		}

		product.getRatings().put(userId, rating);

		productRepository.save(product);
	}

}
