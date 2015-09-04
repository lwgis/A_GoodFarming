package com.zhonghaodi.model;

import java.io.Serializable;

public class Nyscate extends Object{

	/**
	 * 
	 */
	private int id;
	private String name;
	public Nyscate(){
		
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
		return name;
	}
	
}
