package com.zhonghaodi.model;

import java.io.Serializable;



public class Follow implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	
	private User my;
	
	private User user;
	
	private Integer status;
	
	public Follow() {
		// TODO Auto-generated constructor stub
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User getMy() {
		return my;
	}

	public void setMy(User my) {
		this.my = my;
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

	@Override
	public String toString() {
		return "Follow [id=" + id + ", my=" + my + ", user=" + user
				+ ", status=" + status + "]";
	}
	
}
