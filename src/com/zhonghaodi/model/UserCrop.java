package com.zhonghaodi.model;

import java.io.Serializable;

import android.R.integer;

public class UserCrop implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int id;
	
	private Crop crop;
	
	public UserCrop() {
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Crop getCrop() {
		return crop;
	}

	public void setCrop(Crop crop) {
		this.crop = crop;
	}

	@Override
	public String toString() {
		return "UserCrop [id=" + id + ", crop=" + crop + "]";
	}
	
	
}
