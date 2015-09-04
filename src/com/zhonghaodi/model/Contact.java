package com.zhonghaodi.model;

import java.io.Serializable;



public class Contact implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	
	private String name;
	
	private String phone;
	
	private String address;
	
	private String postnumber;
	
	private Integer status;
	
	public Contact(){
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getPostnumber() {
		return postnumber;
	}

	public void setPostnumber(String postnumber) {
		this.postnumber = postnumber;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Contact [id=" + id + ", name=" + name
				+ ", phone=" + phone + ", address=" + address
				+ ", postnumber=" + postnumber + ", status=" + status + "]";
	}
	
}
