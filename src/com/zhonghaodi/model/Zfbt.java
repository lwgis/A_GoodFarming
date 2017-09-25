package com.zhonghaodi.model;

import java.io.Serializable;
import java.util.List;

public class Zfbt extends Second implements Serializable {

	private int coupon;
	private List<NetImage>attachments;
	private List<Stock> stocks;
	private int couponMax;
	private boolean useCoupon;
	private Category category;
	
	public Zfbt() {
		// TODO Auto-generated constructor stub
		super();
	}

	public List<NetImage> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<NetImage> attachments) {
		this.attachments = attachments;
	}

	public List<Stock> getStocks() {
		return stocks;
	}

	public void setStocks(List<Stock> stocks) {
		this.stocks = stocks;
	}

	public boolean isUseCoupon() {
		return useCoupon;
	}

	public void setUseCoupon(boolean useCoupon) {
		this.useCoupon = useCoupon;
	}

	public int getCoupon() {
		return coupon;
	}

	public void setCoupon(int coupon) {
		this.coupon = coupon;
	}

	public int getCouponMax() {
		return couponMax;
	}

	public void setCouponMax(int couponMax) {
		this.couponMax = couponMax;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}
	
}
