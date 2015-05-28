package com.zhonghaodi.model;

import java.io.Serializable;

public class AgrotechnicalAttachment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Agrotechnical agrotechnical;
	private String url;
	private Integer status;
	
	public AgrotechnicalAttachment() {
		// TODO Auto-generated constructor stub
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Agrotechnical getAgrotechnical() {
		return agrotechnical;
	}

	public void setAgrotechnical(Agrotechnical agrotechnical) {
		this.agrotechnical = agrotechnical;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "AgrotechnicalAttachment [id=" + id + ", agrotechnical="
				+ agrotechnical + ", url=" + url + ", status=" + status + "]";
	}
	
}
