package com.zhonghaodi.model;

import java.io.Serializable;
import java.util.List;

public class Zfbt extends Second implements Serializable {

	private Integer coupon;
	private List<NetImage>attachments;
	private List<Stock> stocks;
	
	public Zfbt() {
		// TODO Auto-generated constructor stub
		super();
	}

	public Integer getCoupon() {
		return coupon;
	}

	public void setCoupon(Integer coupon) {
		this.coupon = coupon;
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
	
}
