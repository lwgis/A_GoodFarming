package com.zhonghaodi.model;

import java.io.Serializable;
import java.util.List;

public class Nys extends User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public Nys() {
		// TODO Auto-generated constructor stub
	}


	@Override
	public String toString() {
		return "Nys [crops=" + super.getCrops() + "]";
	}
	
}
