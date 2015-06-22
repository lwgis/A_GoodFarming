package com.zhonghaodi.model;

import java.io.Serializable;


public class Store extends User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Double x;
	
	private Double y;
	
	private Area area;
	
	private Double distance;
	
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

	@Override
	public String toString() {
		return "Store [x=" + x + ", y=" + y + ", area=" + area + ", distance="
				+ distance + "]";
	}
	
}
