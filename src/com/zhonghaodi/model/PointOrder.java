package com.zhonghaodi.model;

import java.io.Serializable;
import java.util.Date;


public class PointOrder implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	
	private Commodity commodity;
	
	private Contact contact;
	
	private Integer count;
	
	private Integer points;
	
	private String time;
	
	private Integer status;
	
	private Express express;
	
	public PointOrder(){
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Commodity getCommodity() {
		return commodity;
	}

	public void setCommodity(Commodity commodity) {
		this.commodity = commodity;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Integer getPoints() {
		return points;
	}

	public void setPoints(Integer points) {
		this.points = points;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Express getExpress() {
		return express;
	}

	public void setExpress(Express express) {
		this.express = express;
	}

	@Override
	public String toString() {
		return "PointOrder [id=" + id + ", commodity=" + commodity + ", contact=" + contact + ", count=" + count
				+ ", points=" + points + ", time=" + time + ", status="
				+ status + "]";
	}
	
}