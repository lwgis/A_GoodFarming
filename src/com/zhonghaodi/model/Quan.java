package com.zhonghaodi.model;

import java.io.Serializable;
import java.util.List;

import com.zhonghaodi.networking.GFDate;


public class Quan implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String content;
	private User writer;
	private Integer status;
	private List<NetImage> attachments;
	private String time;
	private List<Comment> comments;
	
	public Quan(){
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public User getWriter() {
		return writer;
	}

	public void setWriter(User writer) {
		this.writer = writer;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getTime() {
		return GFDate.getStandardDate(time);
	}

	public void setTime(String time) {
		this.time = time;
	}

	public List<NetImage> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<NetImage> attachments) {
		this.attachments = attachments;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	@Override
	public String toString() {
		return "Quan [id=" + id + ", content=" + content + ", writer=" + writer
				+ ", status=" + status + ", attachments=" + attachments
				+ ", time=" + time + ", comments=" + comments + "]";
	}
	
}
