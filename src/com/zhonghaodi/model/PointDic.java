package com.zhonghaodi.model;

import java.io.Serializable;

import android.R.integer;

public class PointDic implements Serializable {

	private int id;
	private String name;
	private int val;
	
	public PointDic(){
		
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

	public int getVal() {
		return val;
	}

	public void setVal(int val) {
		this.val = val;
	}
	
	
	
}
