package com.bebracore.productswatching.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import model.Resource;

@Document
public class Product {
	@Id
	private String id;
	private String title;
	private org.bson.Document characteristics;
	private List<Binary> images;
	private List<String> reviewIds;
	private List<Resource> resources;
	private Type type;
	private Map<String, Integer> ratings;

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

	public org.bson.Document getCharacteristics() {
		return characteristics;
	}

	public void setCharacteristics(org.bson.Document characteristics) {
		this.characteristics = characteristics;
	}

	public List<Binary> getImages() {
		if (images == null) {
			images = new ArrayList<>();
		}
		return images;
	}

	public List<String> getReviewIds() {
		if (reviewIds == null) {
			reviewIds = new ArrayList<>();
		}
		return reviewIds;
	}

	public void setReviewIds(List<String> reviewIds) {
		this.reviewIds = reviewIds;
	}

	public List<Resource> getResources() {
		if (resources == null) {
			resources = new ArrayList<>();
		}
		return resources;
	}

	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Map<String, Integer> getRatings() {
		if (ratings == null) {
			ratings = new LinkedHashMap<>();
		}
		return ratings;
	}

	public void setRatings(Map<String, Integer> ratings) {
		this.ratings = ratings;
	}

	public boolean containsRating(String userId) {
		return ratings != null && ratings.containsKey(userId);
	}

}
