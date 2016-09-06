package com.zhonghaodi.model;

import java.io.Serializable;

public class City implements Serializable {

	private int id;
	private String name;
	private String pname;
	
	public City(){
		
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

	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}
	
	
}
