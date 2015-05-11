package com.zhonghaodi.model;

import java.io.Serializable;

public class NetImage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6485062039899985467L;
	private int id;
	private String url;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
