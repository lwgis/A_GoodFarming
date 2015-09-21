package com.zhonghaodi.model;

import java.io.Serializable;

public class GuaOrder implements Serializable {

	private int id;
	
	private Gua guagua;
	
	private User user;
	
	private Contact contact;
	
	private String time;
	
	private Express express;
	
	private int status;
	
	public GuaOrder(){
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Gua getGuagua() {
		return guagua;
	}

	public void setGuagua(Gua guagua) {
		this.guagua = guagua;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
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
		return "GuaOrder [id=" + id + ", guagua=" + guagua + ", user=" + user
				+ ", contact=" + contact + ", time=" + time + ", status="
				+ status + "]";
	}
	
}
