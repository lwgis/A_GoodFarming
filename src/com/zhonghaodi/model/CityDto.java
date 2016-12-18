package com.zhonghaodi.model;

import java.io.Serializable;

public class CityDto implements Serializable {

	private boolean result;
	private Province zone;
	
	public CityDto(){
		
	}
	

	public boolean isResult() {
		return result;
	}


	public void setResult(boolean result) {
		this.result = result;
	}

	public Province getZone() {
		return zone;
	}


	public void setZone(Province zone) {
		this.zone = zone;
	}


	@Override
	public String toString() {
		return "CityDto [reault=" + result + ", zone=" + zone + "]";
	}
	
}