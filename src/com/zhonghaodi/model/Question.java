package com.zhonghaodi.model;

import java.io.Serializable;
import java.util.List;

import com.zhonghaodi.networking.GFDate;

public class Question implements Serializable {
	private int id;
	private String content;
	private User writer;
	private Crop crop;
	private List<NetImage>attachments;
	private String time;
	private int responsecount;
	private String inform;
	private int status;
	private List<Response> responses;
	private double x;
	private double y;
	private String address;
	public List<Response> getResponses() {
		return responses;
	}

	public void setResponses(List<Response> responses) {
		this.responses = responses;
	}

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
		return GFDate.getStandardDate(time);
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

	public String getInform() {
		return inform;
	}

	public void setInform(String inform) {
		this.inform = inform;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	public String getStime(){
		return time;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
}
