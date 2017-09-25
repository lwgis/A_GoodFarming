package com.zhonghaodi.model;

import java.io.Serializable;

public class Phase implements Serializable {

	private int id;
	private String name;
	
	public Phase() {
		// TODO Auto-generated constructor stub
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
	
}
