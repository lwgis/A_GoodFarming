package com.zhonghaodi.model;

import java.io.Serializable;

public class CityDto implements Serializable {

	private boolean result;
	private City zone;
	
	public CityDto(){
		
	}
	

	public boolean isResult() {
		return result;
	}


	public void setResult(boolean result) {
		this.result = result;
	}


	public City getZone() {
		return zone;
	}

	public void setZone(City zone) {
		this.zone = zone;
	}

	@Override
	public String toString() {
		return "CityDto [reault=" + result + ", zone=" + zone + "]";
	}
	
}