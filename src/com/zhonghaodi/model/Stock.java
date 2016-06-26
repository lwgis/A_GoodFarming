package com.zhonghaodi.model;

import java.io.Serializable;

public class Stock implements Serializable {
	
	private int id;
	
	private Store user;
	
	public Stock(){
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Store getUser() {
		return user;
	}

	public void setUser(Store user) {
		this.user = user;
	}

}
