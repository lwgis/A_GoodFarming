package com.zhonghaodi.model;

import java.io.Serializable;

public class FavStatusDto implements Serializable {

	private boolean result;
	public FavStatusDto() {
		// TODO Auto-generated constructor stub
	}
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	@Override
	public String toString() {
		return "FavStatusDto [result=" + result + "]";
	}
	
}
