package com.zhonghaodi.model;

import java.io.Serializable;

public class ScoringDic implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	
	private String name;
	
	private Integer val;
	
	private Integer status;
	
	public ScoringDic(){
		
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

	public Integer getVal() {
		return val;
	}

	public void setVal(Integer val) {
		this.val = val;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "ScoringDic [id=" + id + ", name=" + name + ", val=" + val
				+ ", status=" + status + "]";
	}
	
}
