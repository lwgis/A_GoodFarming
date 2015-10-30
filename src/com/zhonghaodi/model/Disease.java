package com.zhonghaodi.model;

import java.util.List;

public class Disease {
	private int id;
	private String name;
	private String description;
	private List<NetImage>attachments;
	private String thumbnail;
	private List<Recipe>recipes;
	private List<Solution> solutions;
	private Category_disease category;
	private Crop crop;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<NetImage> getAttachments() {
		return attachments;
	}
	public void setAttachments(List<NetImage> attachments) {
		this.attachments = attachments;
	}
	public String getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Recipe> getRecipes() {
		return recipes;
	}
	public void setRecipes(List<Recipe> recipes) {
		this.recipes = recipes;
	}
	public Category_disease getCategory() {
		return category;
	}
	public void setCategory(Category_disease category) {
		this.category = category;
	}
	public Crop getCrop() {
		return crop;
	}
	public void setCrop(Crop crop) {
		this.crop = crop;
	}
	public List<Solution> getSolutions() {
		return solutions;
	}
	public void setSolutions(List<Solution> solutions) {
		this.solutions = solutions;
	}
	
}
