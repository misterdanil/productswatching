package com.bebracore.productswatching.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.bebracore.productswatching.dao.CustomProductRepository;
import com.bebracore.productswatching.model.Product;
import com.bebracore.productswatching.service.SortType;

import model.Resource;
import model.Review;
import parser.Type;

@Repository
public class ProductRepositoryImpl implements CustomProductRepository {
	@Autowired
	private MongoOperations mongoOperations;

	public List<Product> getSmartphones(List<String> brands, Integer start, Integer limit, List<String> rams,
			String rom, Double diagonalFrom, Double diagonalTo, Double fromPrice, Double toPrice, SortType sort,
			String text) {
		org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();

		if (brands != null && !brands.isEmpty()) {
			query.addCriteria(Criteria.where("characteristics.brand").in(brands));
		}
		if (start != null) {
			query.skip(start);
		}
		if (limit != null) {
			query.limit(limit);
		}
		if (rams != null && !rams.isEmpty()) {
			query.addCriteria(Criteria.where("characteristics.ram.value").in(rams));
		}
		if (rom != null) {
			query.addCriteria(Criteria.where("characteristics.rom.value").is(rom));
		}

		Criteria diagonalCriteria = null;
		if (diagonalFrom != null || diagonalTo != null) {
			diagonalCriteria = Criteria.where("characteristics.screen.diagonal");
			query.addCriteria(diagonalCriteria);
		}
		if (diagonalFrom != null) {
			diagonalCriteria.gte(diagonalFrom);
		}
		if (diagonalTo != null) {
			diagonalCriteria.lte(diagonalTo);
		}

		Criteria priceCriteria = null;
		if (fromPrice != null || toPrice != null) {
			priceCriteria = Criteria.where("resources.price");
			query.addCriteria(priceCriteria);
		}
		if (fromPrice != null) {
			priceCriteria.gte(fromPrice);
		}
		if (toPrice != null) {
			priceCriteria.lte(toPrice);
		}

		if (sort != null) {
			if (sort.equals(SortType.CHEAP)) {
				query.with(Sort.by(Sort.Direction.DESC, "resources.price"));
			} else if (sort.equals(SortType.EXPENSIVE)) {
				query.with(Sort.by(Sort.Direction.ASC, "resources.price"));
			}
		}

		if (text != null && !text.isEmpty()) {
			query.addCriteria(
					Criteria.where("title").regex(Pattern.compile(Pattern.quote(text), Pattern.CASE_INSENSITIVE)));
		}

		return mongoOperations.find(query, Product.class);
	}

	@Override
	public List<Resource> findResources(String id) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));

		Product product = mongoOperations.findOne(query, Product.class);

		return product.getResources();
	}

	@Override
	public List<Review> getProductResourceReviews(String id, Type type) {
		Query query = new Query();

		query.addCriteria(Criteria.where("id").is(id));
//		query.addCriteria(Criteria.where("resources.type").is(type.toString()));
//		quermy.fields().include("resource.reviews");

		Product product = mongoOperations.findOne(query, Product.class);
		List<Review> reviews = new ArrayList<>();
		product.getResources().forEach(res -> {
			reviews.addAll(res.getReviews());
		});

		return reviews;
	}

}
