package com.zhonghaodi.model;

import java.io.Serializable;

public class Category_disease implements Serializable {

	private int id;
	private String name;
	
	public Category_disease(){
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Category_disease [id=" + id + ", name=" + name + "]";
	}
	
}
