package com.bebracore.productswatching.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bebracore.productswatching.dto.ProductDto;
import com.bebracore.productswatching.dto.generator.ProductDtoGenerator;
import com.bebracore.productswatching.model.Product;
import com.bebracore.productswatching.service.ProductService;
import com.bebracore.productswatching.service.SortType;
import com.bebracore.productswatching.service.error.ProductAlreadyAddedException;
import com.bebracore.productswatching.service.error.ProductNotFoundException;
import com.bebracore.productswatching.service.error.RatedAlreadyException;
import com.bebracore.webconfig.controller.AbstractController;
import com.bebracore.webconfig.dto.ValidatedResponse;

import dto.ResourceDto;
import dto.ResourceDtoGenerator;
import jakarta.servlet.http.HttpServletResponse;
import model.Review;
import parser.Type;

@Controller
public class SmartphoneController extends AbstractController {
	@Autowired
	private ProductService productService;

	@GetMapping(value = "/product/smartphones", produces = "application/json; charset=utf-8")
	@ResponseBody
	public List<ProductDto> getProducts(@RequestParam(required = false) List<String> brands,
			@RequestParam(required = false, defaultValue = "0") Integer start,
			@RequestParam(required = false, defaultValue = "25") Integer limit,
			@RequestParam(required = false) List<String> rams, @RequestParam(required = false) String rom,
			@RequestParam(required = false) Double diagonalFrom, @RequestParam(required = false) Double diagonalTo,
			@RequestParam(required = false) Double fromPrice, @RequestParam(required = false) Double toPrice,
			@RequestParam(required = false, defaultValue = "CHEAP") String sort,
			@RequestParam(required = false) String text) {
		System.out.println(rams);
		List<Product> products = productService.getSmartphones(brands, start, limit, rams, rom, diagonalFrom,
				diagonalTo, fromPrice, toPrice, SortType.valueOf(sort), text);

		return ProductDtoGenerator.createDtos(products);
	}

	@GetMapping(value = "/product/smartphones/{id}/image")
	public void getProductImage(@PathVariable String id, HttpServletResponse response) {
		Product product = productService.getProductById(id);

		Binary image = product.getImages().get(1);

		response.setContentType(MediaType.IMAGE_JPEG_VALUE);

		try {
			response.getOutputStream().write(image.getData());
		} catch (IOException e) {
			throw new RuntimeException(
					"Exception occurred while getting image. Couldn't get image of smartphone with id: " + id, e);
		}
	}

	@GetMapping(value = "/product/smartphone/{id}", produces = "application/json; charset=utf-8")
	@ResponseBody
	public ResponseEntity<ProductDto> getSmartphone(@PathVariable String id) {
		Product product = productService.getProductById(id);
		if (product == null) {
			return ResponseEntity.notFound().build();
		}
		ProductDto dto = ProductDtoGenerator.createDto(product);
		return ResponseEntity.ok(dto);
	}

	@GetMapping(value = "/product/smartphone/{id}/resources", produces = "application/json; charset=utf-8")
	@ResponseBody
	public List<ResourceDto> getProductResources(@PathVariable String id) {

		Product product = productService.getProductById(id);

		return ResourceDtoGenerator.createDtos(product.getResources());
	}

	@GetMapping("/product/smartphone/{id}/resources/{type}/reviews")
	@ResponseBody
	public List<Review> getProductResourceReviews(@PathVariable String id, @PathVariable Type type) {
		Product product = productService.getProductById(id);
		List<Review> reviews = new ArrayList<>();
		product.getResources().forEach(res -> {
			res.getReviews().forEach(rev -> rev.setType(res.getType().toString()));
			reviews.addAll(res.getReviews());
		});
		return reviews;
	}

	@PostMapping("/product/smartphone/{id}/favorite")
	public ResponseEntity<ValidatedResponse> addToFavorite(@PathVariable String id) {
		try {
			productService.addToFavorite(id);
		} catch (ProductAlreadyAddedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PostMapping("/product/smartphone/{id}/rating/{rating}")
	public ResponseEntity<ValidatedResponse> rate(@PathVariable String id, @PathVariable Integer raing) {
		BindingResult result = new MapBindingResult(new HashMap<>(), "product");

		try {
			productService.rate(id, raing);
		} catch (ProductNotFoundException e) {
			result.rejectValue("id", "product.notFound", "Нет такого товара");
			return ResponseEntity.badRequest().body(createErrorValidationResponse(result));
		} catch (RatedAlreadyException e) {
			result.rejectValue("rating", "rating.exist", "Вы уже оставили голос");
			return ResponseEntity.badRequest().body(createErrorValidationResponse(result));
		}

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
