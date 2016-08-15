package com.zhonghaodi.model;

import java.io.Serializable;
import java.util.List;

public class Caicaicai implements Serializable {

	private int id;
	private String content;
	private String cimage;
	private String time;
	private String endTime;
	private int status;
	private String zone;
	private List<NetImage> attachments1;
	private int responseCount;
	private int commentCount;
	private String showAnswer;
	private String showAimage;
	private List<NetImage> showAttachments2;
	private List<CaiResponse> responses;
	private List<CaiComment> comments;
	private List<NetImage> allAttachments;
	
	public Caicaicai(){
		
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

	public String getCimage() {
		return cimage;
	}

	public void setCimage(String cimage) {
		this.cimage = cimage;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public List<NetImage> getAttachments1() {
		return attachments1;
	}

	public void setAttachments1(List<NetImage> attachments1) {
		this.attachments1 = attachments1;
	}

	public int getResponseCount() {
		return responseCount;
	}

	public void setResponseCount(int responseCount) {
		this.responseCount = responseCount;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public String getShowAnswer() {
		return showAnswer;
	}

	public void setShowAnswer(String showAnswer) {
		this.showAnswer = showAnswer;
	}

	public String getShowAimage() {
		return showAimage;
	}

	public void setShowAimage(String showAimage) {
		this.showAimage = showAimage;
	}

	public List<NetImage> getShowAttachments2() {
		return showAttachments2;
	}

	public void setShowAttachments2(List<NetImage> showAttachments2) {
		this.showAttachments2 = showAttachments2;
	}

	public List<CaiResponse> getResponses() {
		return responses;
	}

	public void setResponses(List<CaiResponse> responses) {
		this.responses = responses;
	}

	public List<CaiComment> getComments() {
		return comments;
	}

	public void setComments(List<CaiComment> comments) {
		this.comments = comments;
	}

	public List<NetImage> getAllAttachments() {
		return allAttachments;
	}

	public void setAllAttachments(List<NetImage> allAttachments) {
		this.allAttachments = allAttachments;
	}
}
