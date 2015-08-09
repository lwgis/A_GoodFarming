package com.zhonghaodi.model;

import java.io.Serializable;
import java.util.List;


public class Store extends User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Double x;
	
	private Double y;
	
	private Area area;
	
	private Double distance;
	
	private List<Recipe> recipes;
	
	private Double scoring;
	
	public Store() {
		// TODO Auto-generated constructor stub
	}

	public Double getX() {
		return x;
	}

	public void setX(Double x) {
		this.x = x;
	}

	public Double getY() {
		return y;
	}

	public void setY(Double y) {
		this.y = y;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public List<Recipe> getRecipes() {
		return recipes;
	}

	public void setRecipes(List<Recipe> recipes) {
		this.recipes = recipes;
	}

	public Double getScoring() {
		return scoring;
	}

	public void setScoring(Double scoring) {
		this.scoring = scoring;
	}

	@Override
	public String toString() {
		return "Store [x=" + x + ", y=" + y + ", area=" + area + ", distance="
				+ distance + "]";
	}
	
}
