package com.zhonghaodi.model;

import java.io.Serializable;

public class ZfbtOrder implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String usid;
	private User user;
	private Zfbt second;
	private Integer status;
	private Double price;
	private String time;
	
	
	public ZfbtOrder() {
		// TODO Auto-generated constructor stub
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsid() {
		return usid;
	}

	public void setUsid(String usid) {
		this.usid = usid;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Zfbt getSecond() {
		return second;
	}

	public void setSecond(Zfbt second) {
		this.second = second;
	}

	@Override
	public String toString() {
		return "SecondOrder [id=" + id + ", usid=" + usid + ", user=" + user
				+ ", second=" + second + ", status=" + status + ", price="
				+ price + ", time=" + time + "]";
	}
}
