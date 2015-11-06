package com.zhonghaodi.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.zhonghaodi.networking.GFDate;


public class Agrotechnical implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String title;
	private String content;
	private String time;
	private Integer status;
	private List<NetImage> attachments;
	private String thumbnail;
	private Category_disease category;
	
	public Agrotechnical() {
		// TODO Auto-generated constructor stub
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return GFDate.getStandardDate(time);
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

	public List<NetImage> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<NetImage> attachments) {
		this.attachments = attachments;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public Category_disease getCategory() {
		return category;
	}

	public void setCategory(Category_disease category) {
		this.category = category;
	}

	@Override
	public String toString() {
		return "Agrotechnical [id=" + id + ", title=" + title + ", content="
				+ content + ", time=" + time + ", status=" + status
				+ ", attachments=" + attachments + ", thumbnail=" + thumbnail
				+ "]";
	}
	
}
