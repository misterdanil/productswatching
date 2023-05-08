package com.bebracore.productswatching.dto;

import java.util.HashMap;
import java.util.Map;

import org.bson.Document;

public class ProductDto {
	private String id;
	private String title;
	private Double fromPrice;
	private Double toPrice;
	private Double averageRating;
	private Document characteristics;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Double getFromPrice() {
		return fromPrice;
	}

	public void setFromPrice(Double fromPrice) {
		this.fromPrice = fromPrice;
	}

	public Double getToPrice() {
		return toPrice;
	}

	public void setToPrice(Double toPrice) {
		this.toPrice = toPrice;
	}

	public Double getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(Double averageRating) {
		this.averageRating = averageRating;
	}

	public Document getCharacteristics() {
		return characteristics;
	}

	public void setCharacteristics(Document characteristics) {
		this.characteristics = characteristics;
	}

}
