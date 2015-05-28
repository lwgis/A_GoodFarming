package com.zhonghaodi.model;

import java.io.Serializable;
import java.util.List;

public class Nys extends User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<UserCrop> crops;
	
	public List<UserCrop> getCrops() {
		return crops;
	}


	public void setCrops(List<UserCrop> crops) {
		this.crops = crops;
	}


	public Nys() {
		// TODO Auto-generated constructor stub
	}


	@Override
	public String toString() {
		return "Nys [crops=" + crops + "]";
	}
	
}
