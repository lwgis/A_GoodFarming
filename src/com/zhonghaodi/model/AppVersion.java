package com.zhonghaodi.model;

import java.io.Serializable;

import android.R.integer;

public class AppVersion implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String title;
	private int version;
	private String url;
	
	public AppVersion(){
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "AppVersion [id=" + id + ", title=" + title + ", version="
				+ version + ", url=" + url + "]";
	}
	
}
