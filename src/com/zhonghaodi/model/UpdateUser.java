package com.zhonghaodi.model;

import java.util.List;

public class UpdateUser {
	private User user;
	private Level level;
	private String description;
	private double x;
	private double y;
	private String name;
	private List<NetImage> attachments;
	private List<Crop> crops;
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<NetImage> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<NetImage> attachments) {
		this.attachments = attachments;
	}

	public List<Crop> getCrops() {
		return crops;
	}

	public void setCrops(List<Crop> crops) {
		this.crops = crops;
	}
}
