package com.zhonghaodi.model;

import java.io.Serializable;

public class Advertising extends NetImage {
	
	private String starttime;
	private String endtime;
	private int sort;
	
	public Advertising(){
		
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}
	
}
