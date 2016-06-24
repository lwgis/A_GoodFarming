package com.zhonghaodi.model;


public class Function {
	private String name;
	private Class<?> activityClass;
	private int imageId;
	private String description;
	public int getImageId() {
		return imageId;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

	public Function(String name,Class<?> activityClass ,int imageId) {
		this.name=name;
		this.setActivityClass(activityClass);
		this.setImageId(imageId);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Class<?> getActivityClass() {
		return activityClass;
	}

	public void setActivityClass(Class<?> activityClass) {
		this.activityClass = activityClass;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


}
