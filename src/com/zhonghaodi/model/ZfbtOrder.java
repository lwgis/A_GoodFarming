package com.zhonghaodi.model;

import java.io.Serializable;

public class ZfbtOrder extends SecondOrder implements Serializable {

	private Store nzd;
	private Contact contact;
	private int coupon;
	
	public ZfbtOrder(){
		super();
	}

	public Store getNzd() {
		return nzd;
	}

	public void setNzd(Store nzd) {
		this.nzd = nzd;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public int getCoupon() {
		return coupon;
	}

	public void setCoupon(int coupon) {
		this.coupon = coupon;
	}
	
}
