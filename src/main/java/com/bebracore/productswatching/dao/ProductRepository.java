package com.bebracore.productswatching.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.bebracore.productswatching.model.Product;

import model.Resource;
import model.Review;
import parser.Type;

@Repository
public interface ProductRepository extends MongoRepository<Product, String>, CustomProductRepository {
	@Query("{'characteristics.brand' : ?0, 'characteristics.series': ?1, 'characteristics.ram.value' : ?2, 'characteristics.ram.measure' : ?3, 'characteristics.rom.value' : ?4, 'characteristics.rom.measure' : ?5}")
	Product findByBrandAndSeriesAndRamAndRom(String brand, String series, String ram, String ramMeasure, String rom,
			String romMeasure);

	Product findOneById(String id);

	boolean existsById(String id);

//	@Query("{'characteristics.brand': {'$regex' : ?0, $options: 'i'}, 'characteristics.ram.value': ?2}")
//	List<Product> getSmartphones(String brand, Integer limit, String ram, String rom, Double diagonalFrom,
//			Double diagonalTo, Double fromPrice, Double toPrice) 

}
