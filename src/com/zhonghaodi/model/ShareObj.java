package com.zhonghaodi.model;

import java.io.Serializable;

public class ShareObj implements Serializable {

	private String url;
	
	public ShareObj(){
		
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
	
}
