package com.zhonghaodi.model;

import java.util.List;

public class Question {
	private int id;
	private String content;
	private User writer;
	private Crop crop;
	private List<NetImage>attachments;
	private String time;
	private int responsecount;
	public int getId() {
		return id;
	}

	public void setId(int id) {
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

	public Crop getCrop() {
		return crop;
	}

	public void setCrop(Crop crop) {
		this.crop = crop;
	}

	public List<NetImage> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<NetImage> attachments) {
		this.attachments = attachments;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getResponsecount() {
		return responsecount;
	}

	public void setResponsecount(int responsecount) {
		this.responsecount = responsecount;
	}

}
