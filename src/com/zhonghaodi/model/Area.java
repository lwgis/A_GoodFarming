package com.zhonghaodi.model;

import java.io.Serializable;
import java.util.List;

public class Area implements Serializable {

	private Integer id;
	
	private String text;
	
	private Integer parentid;
	
	private Integer status;
	
	public Area() {
		// TODO Auto-generated constructor stub
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getParentid() {
		return parentid;
	}

	public void setParentid(Integer parentid) {
		this.parentid = parentid;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
