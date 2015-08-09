package com.zhonghaodi.model;

import java.io.Serializable;


public class Recipe implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String title;
	private String description;
	private Store nzd;
	private String thumbnail;
	private double price;
	private double newprice;
	private int haocount;
	private int zhongcount;
	private int chacount;
	private int sumcount;
	
	public Recipe() {
		// TODO Auto-generated constructor stub
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Store getNzd() {
		return nzd;
	}
	public void setNzd(Store nzd) {
		this.nzd = nzd;
	}
	public String getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public double getNewprice() {
		return newprice;
	}
	public void setNewprice(double newprice) {
		this.newprice = newprice;
	}

	public int getHaocount() {
		return haocount;
	}

	public void setHaocount(int haocount) {
		this.haocount = haocount;
	}

	public int getZhongcount() {
		return zhongcount;
	}

	public void setZhongcount(int zhongcount) {
		this.zhongcount = zhongcount;
	}

	public int getChacount() {
		return chacount;
	}

	public void setChacount(int chacount) {
		this.chacount = chacount;
	}

	public int getSumcount() {
		return sumcount;
	}

	public void setSumcount(int sumcount) {
		this.sumcount = sumcount;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
	
}
