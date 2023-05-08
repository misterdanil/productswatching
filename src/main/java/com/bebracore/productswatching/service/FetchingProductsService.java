package com.bebracore.productswatching.service;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.bson.Document;
import org.bson.json.JsonObject;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bebracore.productswatching.model.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import model.Resource;
import model.Review;
import parser.EldoradoSmartphoneParser;
import parser.MvideoParser;
import parser.Parser;
import parser.Type;

@Service
public class FetchingProductsService {
	@Autowired
	private ProductService productService;

	private ObjectMapper mapper = new ObjectMapper();

	public void fetchResources(String link, Type type, InfoType infoType) {
		Parser parser = getParser(type);
		parser.createWebDriver();
		int page = 1;
		List<Resource> resources = null;
//		System.out.println(resources);
		while (page != 10) {
			page++;
			resources = parser.getResources(link, page);
			resources.forEach(resource -> {
				resource.setType(type);

				System.out.println("Start iterating by products");
				String brand = (String) resource.getAttribute("brand");
				String series = (String) resource.getAttribute("series");

				Document ramNode = (Document) resource.getAttribute("ram");
				System.out.println(ramNode);

				Document romNode = (Document) resource.getAttribute("rom");

				System.out.println(romNode);
				System.out.println("fetching by brand, series, ram and rom");
				Product product = productService.findByBrandAndSeriesAndRamAndRom(brand, series,
						(String) ramNode.getOrDefault("value", null), (String) ramNode.getOrDefault("measure", null),
						(String) romNode.getOrDefault("value", null), (String) romNode.getOrDefault("measure", null));

				if (product == null) {
					System.out.println("not found");
					if (infoType.equals(InfoType.SHORT)) {
						System.out.println("getting full resource by " + resource.getLink());
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						resource = parser.getResource(resource);
						if (resource == null) {
							return;
						}
					}
					Product newProduct = new Product();
					product = newProduct;

					resource.getImages().forEach(image -> {
						try {
							newProduct.getImages().add(new Binary(new URL(image).openStream().readAllBytes()));
						} catch (IOException e) {
							System.out.println("Couldn't download image " + image);
						}
					});
					newProduct.setTitle(resource.getName());
					newProduct.getResources().add(resource);

					String attributes;
					try {
						attributes = mapper.writeValueAsString(resource.getAttributes());
					} catch (JsonProcessingException e) {
						e.printStackTrace();
						System.out.println("Couldn't transform map attributes to string");

						return;
					}

					newProduct.setCharacteristics(Document.parse(attributes));
					System.out.println(Document.parse(attributes));
					System.out.println("no");
					System.out.println(attributes);
					System.out.println("getting reviews");
//					List<Review> reviews = parser.getReviews(resource);
//					resource.getReviews().addAll(reviews);

					System.out.println("saving...");
					productService.save(product);

					return;
				}

				System.out.println("found " + resource.getName());
				Resource foundResource = findResource(product, type);

				if (foundResource == null) {
					product.getResources().add(resource);
				} else {
					if (resource.getColors() != null && !resource.getColors().isEmpty()) {
						String key = resource.getColors().entrySet().iterator().next().getKey();
						if (!foundResource.getColors().containsKey(key)) {
							foundResource.getColors().put(key, resource.getColors().get(key));
						}
					}
					resource = foundResource;
				}

//				List<Review> reviews = parser.getReviews(resource);
//				if (isExist) {
//					for (int i = 0; i < reviews.size(); i++) {
//						Review review = findReview(reviews.get(i).getId(), resource);
//						if (review == null) {
//							resource.addReview(review);
//						}
//					}
//				} else {
//					resource.getReviews().addAll(reviews);
//				}

				productService.save(product);
			});
		}
		parser.finishWebDriver();
	}

	public List<Review> fetchReviews(Resource resource) {
		Parser parser;
		if (resource.getType().equals(Type.MVIDEO)) {
			parser = new MvideoParser();
		} else {
//			parser = new EldoradoSmartphoneParser();
//			parser.createWebDriver();
			return null;
		}
		List<Review> reviews = parser.getReviews(resource);
		parser.finishWebDriver();
		return reviews;
	}

	private Review findReview(String id, Resource resource) {
		for (int i = 0; i < resource.getReviews().size(); i++) {
			if (resource.getReviews().get(i).getId().equals(id)) {
				return resource.getReviews().get(i);
			}
		}
		return null;
	}

	private Resource findResource(Product product, Type type) {
		for (int i = 0; i < product.getResources().size(); i++) {
			if (product.getResources().get(i).getType().equals(type)) {
				return product.getResources().get(i);
			}
		}
		return null;
	}

	private Parser getParser(Type type) {
		if (type.equals(Type.MVIDEO)) {
			return new MvideoParser();
		} else if (type.equals(Type.ELDORADO)) {
			return new EldoradoSmartphoneParser();
		}
		return null;
	}
}
