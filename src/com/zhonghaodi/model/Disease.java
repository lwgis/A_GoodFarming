package com.zhonghaodi.model;

import java.util.List;

public class Disease {
	private int id;
	private String name;
	private String description;
	private List<NetImage>attachments;
	private String thumbnail;
	private List<Recipe>recipes;
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
}
