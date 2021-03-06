package com.zhonghaodi.model;

import java.io.Serializable;
import java.util.List;


public class Commodity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	
	private String name;
	
	private String image;
	
	private Integer point;
	
	private String description;
	
	private Integer status;
	
	private List<Level> levels;
	
	private Integer tjcoin;
	
	private Integer exchange;
	
	public Commodity(){
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Integer getPoint() {
		return point;
	}

	public void setPoint(Integer point) {
		this.point = point;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public List<Level> getLevels() {
		return levels;
	}

	public void setLevels(List<Level> levels) {
		this.levels = levels;
	}	

	public Integer getTjcoin() {
		return tjcoin;
	}

	public void setTjcoin(Integer tjcoin) {
		this.tjcoin = tjcoin;
	}

	public Integer getExchange() {
		return exchange;
	}

	public void setExchange(Integer exchange) {
		this.exchange = exchange;
	}

	@Override
	public String toString() {
		return "Commodity [id=" + id + ", name=" + name + ", image=" + image
				+ ", point=" + point + ", description=" + description
				+ ", status=" + status + "]";
	}
	
}
